package com.ecostream.anomaly.service;

import com.ecostream.anomaly.dto.AnalysisResultDto;
import com.ecostream.anomaly.dto.AnomalyAlertDto;
import com.ecostream.anomaly.dto.EnergyReadingDto;
import com.ecostream.anomaly.exception.AnomalyNotFoundException;
import com.ecostream.anomaly.model.AnomalyAlert;
import com.ecostream.anomaly.repository.AnomalyAlertRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnomalyService {

    private final AnomalyAlertRepository repository;

    @Value("${anomaly.threshold.high:1000.0}")
    private double highThreshold;

    @Value("${anomaly.threshold.medium:500.0}")
    private double mediumThreshold;

    /**
     * Analyse une mesure et détecte une anomalie éventuelle.
     * Persiste uniquement si anomalie détectée (HIGH ou MEDIUM).
     */
    public AnalysisResultDto analyze(EnergyReadingDto dto) {
        log.info("Analyse : meterId={}, consumption={}kWh", dto.getMeterId(), dto.getConsumption());

        String severity;
        String message;
        double threshold;

        if (dto.getConsumption() >= highThreshold) {
            severity = "HIGH";
            threshold = highThreshold;
            message = String.format(
                    "Consommation CRITIQUE : %.2f kWh dépasse le seuil haut de %.0f kWh",
                    dto.getConsumption(), highThreshold);

        } else if (dto.getConsumption() >= mediumThreshold) {
            severity = "MEDIUM";
            threshold = mediumThreshold;
            message = String.format(
                    "Consommation ÉLEVÉE : %.2f kWh dépasse le seuil moyen de %.0f kWh",
                    dto.getConsumption(), mediumThreshold);

        } else {
            severity = "NORMAL";
            threshold = mediumThreshold;
            message = String.format(
                    "Consommation normale : %.2f kWh", dto.getConsumption());
        }

        boolean isAnomaly = !severity.equals("NORMAL");

        if (isAnomaly) {
            AnomalyAlert alert = AnomalyAlert.builder()
                    .meterId(dto.getMeterId())
                    .consumption(dto.getConsumption())
                    .severityLevel(severity)
                    .message(message)
                    .threshold(threshold)
                    .timestamp(dto.getTimestamp())
                    .detectedAt(LocalDateTime.now())
                    .build();

            repository.save(alert);
            log.warn("Anomalie {} détectée — meterId={}, consumption={}kWh",
                    severity, dto.getMeterId(), dto.getConsumption());
        } else {
            log.debug("Mesure normale — meterId={}", dto.getMeterId());
        }

        return AnalysisResultDto.builder()
                .meterId(dto.getMeterId())
                .consumption(dto.getConsumption())
                .severityLevel(severity)
                .anomalyDetected(isAnomaly)
                .message(message)
                .build();
    }

    /**
     * Toutes les alertes enregistrées (toutes sévérités).
     */
    public List<AnomalyAlertDto> getAllAlerts() {
        return repository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Alertes filtrées par compteur.
     */
    public List<AnomalyAlertDto> getAlertsByMeter(String meterId) {
        List<AnomalyAlert> alerts = repository.findByMeterIdOrderByDetectedAtDesc(meterId);
        if (alerts.isEmpty()) {
            throw new AnomalyNotFoundException("Aucune anomalie trouvée pour le compteur : " + meterId);
        }
        return alerts.stream().map(this::toDto).collect(Collectors.toList());
    }

    /**
     * Alertes filtrées par niveau de sévérité.
     */
    public List<AnomalyAlertDto> getAlertsBySeverity(String severityLevel) {
        return repository.findBySeverityLevelOrderByDetectedAtDesc(severityLevel.toUpperCase())
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Une alerte par son id.
     */
    public AnomalyAlertDto getAlertById(Long id) {
        AnomalyAlert alert = repository.findById(id)
                .orElseThrow(() -> new AnomalyNotFoundException("Alerte introuvable : id=" + id));
        return toDto(alert);
    }

    // ── Mapper ────────────────────────────────────────────────────

    private AnomalyAlertDto toDto(AnomalyAlert a) {
        return AnomalyAlertDto.builder()
                .id(a.getId())
                .meterId(a.getMeterId())
                .consumption(a.getConsumption())
                .severityLevel(a.getSeverityLevel())
                .message(a.getMessage())
                .threshold(a.getThreshold())
                .timestamp(a.getTimestamp())
                .detectedAt(a.getDetectedAt())
                .build();
    }
}
