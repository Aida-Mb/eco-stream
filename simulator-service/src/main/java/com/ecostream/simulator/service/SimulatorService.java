package com.ecostream.simulator.service;

import com.ecostream.simulator.dto.EnergyReadingDto;
import com.ecostream.simulator.dto.SimulatorStatusDto;
import com.ecostream.simulator.dto.StartRequest;
import com.ecostream.simulator.model.MeterConfig;
import com.ecostream.simulator.repository.MeterConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Slf4j
public class SimulatorService {

    private final RestTemplate restTemplate;
    private final MeterConfigRepository meterConfigRepository;

    @Value("${ingestion.service.url}")
    private String ingestionServiceUrl;

    @Value("${simulator.min-consumption:0.5}")
    private double minConsumption;

    @Value("${simulator.max-consumption:15.0}")
    private double maxConsumption;

    // ── État interne ──────────────────────────────────────────────
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final AtomicInteger readingsSent = new AtomicInteger(0);
    private final List<ScheduledFuture<?>> scheduledTasks = new ArrayList<>();
    private final Random random = new Random();

    private int currentMeterCount = 0;
    private int currentIntervalMs = 2000;

    // Scheduler dédié (indépendant du scheduler Spring principal)
    private final ThreadPoolTaskScheduler taskScheduler = createScheduler();

    // ── API publique ──────────────────────────────────────────────

    /**
     * Démarre la simulation avec N compteurs, chacun envoyant
     * un relevé toutes les intervalMs millisecondes.
     */
    public SimulatorStatusDto start(StartRequest request) {
        if (running.get()) {
            stop(); // Arrêt propre avant redémarrage
        }

        currentMeterCount = request.getMeterCount();
        currentIntervalMs = request.getIntervalMs();
        readingsSent.set(0);

        // Crée ou réactive les compteurs en BDD
        provisionMeters(request.getMeterCount());

        // Récupère tous les compteurs actifs
        List<MeterConfig> meters = meterConfigRepository.findAllByActiveTrue();

        // Planifie une tâche par compteur
        for (MeterConfig meter : meters) {
            ScheduledFuture<?> future = taskScheduler.scheduleAtFixedRate(
                    () -> sendReading(meter.getMeterId()),
                    Duration.ofMillis(request.getIntervalMs())
            );
            scheduledTasks.add(future);
        }

        running.set(true);
        log.info("Simulation started: {} meters, interval={}ms", meters.size(), request.getIntervalMs());

        return buildStatus();
    }

    /**
     * Arrête proprement toutes les tâches planifiées.
     */
    public SimulatorStatusDto stop() {
        scheduledTasks.forEach(f -> f.cancel(false));
        scheduledTasks.clear();
        running.set(false);
        log.info("Simulation stopped. Total readings sent: {}", readingsSent.get());
        return buildStatus();
    }

    /**
     * Retourne l'état courant du simulateur.
     */
    public SimulatorStatusDto getStatus() {
        return buildStatus();
    }

    // ── Logique interne ───────────────────────────────────────────

    /**
     * Génère et envoie un relevé pour un compteur donné vers l'Ingestion Service.
     */
    private void sendReading(String meterId) {
        EnergyReadingDto reading = EnergyReadingDto.builder()
                .meterId(meterId)
                .consumption(generateConsumption())
                .timestamp(Instant.now().toString())
                .build();

        try {
            restTemplate.postForEntity(
                    ingestionServiceUrl + "/readings",
                    reading,
                    Void.class
            );
            readingsSent.incrementAndGet();
            log.debug("Reading sent — meterId={}, consumption={}kWh", meterId, reading.getConsumption());

        } catch (RestClientException e) {
            log.warn("Failed to send reading for meterId={}: {}", meterId, e.getMessage());
            // On ne stoppe pas la simulation, on log et on continue
        }
    }

    /**
     * Génère une consommation aléatoire entre min et max,
     * avec une petite chance (10%) de générer une valeur anormalement haute
     * pour déclencher l'Anomaly Detection Service.
     */
    private double generateConsumption() {
        boolean isAnomaly = random.nextDouble() < 0.10; // 10% de chance d'anomalie
        double value;

        if (isAnomaly) {
            // Valeur 4–6× au-dessus du max normal pour déclencher une alerte
            value = maxConsumption * (4 + random.nextDouble() * 2);
        } else {
            value = minConsumption + random.nextDouble() * (maxConsumption - minConsumption);
        }

        // Arrondi à 2 décimales
        return Math.round(value * 100.0) / 100.0;
    }

    /**
     * Crée les entrées MeterConfig en BDD si elles n'existent pas encore.
     * Format des IDs : house-1, house-2, ..., house-N
     */
    private void provisionMeters(int count) {
        // Désactive tous les compteurs existants d'abord
        meterConfigRepository.findAll().forEach(m -> {
            m.setActive(false);
            meterConfigRepository.save(m);
        });

        for (int i = 1; i <= count; i++) {
            String meterId = "house-" + i;
            if (!meterConfigRepository.existsByMeterId(meterId)) {
                MeterConfig meter = MeterConfig.builder()
                        .meterId(meterId)
                        .intervalMs(currentIntervalMs)
                        .active(true)
                        .build();
                meterConfigRepository.save(meter);
            } else {
                // Réactive le compteur existant
                meterConfigRepository.findAll().stream()
                        .filter(m -> m.getMeterId().equals(meterId))
                        .findFirst()
                        .ifPresent(m -> {
                            m.setActive(true);
                            m.setIntervalMs(currentIntervalMs);
                            meterConfigRepository.save(m);
                        });
            }
        }
    }

    private SimulatorStatusDto buildStatus() {
        return SimulatorStatusDto.builder()
                .status(running.get() ? "RUNNING" : "STOPPED")
                .meterCount(currentMeterCount)
                .intervalMs(currentIntervalMs)
                .readingsSent(readingsSent.get())
                .build();
    }

    private ThreadPoolTaskScheduler createScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(20); // Supporte jusqu'à 20 compteurs simultanés
        scheduler.setThreadNamePrefix("meter-sim-");
        scheduler.initialize();
        return scheduler;
    }
}
