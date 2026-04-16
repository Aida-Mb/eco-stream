package com.ecostream.ingestion.service;

import com.ecostream.ingestion.dto.EnergyReadingDto;
import com.ecostream.ingestion.dto.EnergyReadingResponseDto;
import com.ecostream.ingestion.model.EnergyReading;
import com.ecostream.ingestion.repository.EnergyReadingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class IngestionService {

    private final EnergyReadingRepository repository;
    private final RestTemplate restTemplate;

    @Value("${billing.service.url}")
    private String billingServiceUrl;

    @Value("${anomaly.service.url}")
    private String anomalyServiceUrl;

    /**
     * Reçoit une mesure, la persiste, puis appelle
     * Billing et Anomaly en parallèle via deux threads.
     */
    public EnergyReadingResponseDto ingest(EnergyReadingDto dto) {
        // 1. Persister la mesure
        EnergyReading saved = repository.save(
                EnergyReading.builder()
                        .meterId(dto.getMeterId())
                        .consumption(dto.getConsumption())
                        .timestamp(dto.getTimestamp())
                        .build()
        );
        log.info("Reading saved — id={}, meterId={}, consumption={}kWh",
                saved.getId(), saved.getMeterId(), saved.getConsumption());

        // 2. Appels vers Billing et Anomaly en parallèle
        Thread billingThread = new Thread(() -> forwardToBilling(dto));
        Thread anomalyThread = new Thread(() -> forwardToAnomaly(dto));

        billingThread.start();
        anomalyThread.start();

        // On n'attend pas les réponses (fire-and-forget)
        // Si tu veux attendre : billingThread.join(); anomalyThread.join();

        return toResponseDto(saved);
    }

    /**
     * Retourne toutes les mesures avec pagination.
     * page=0, size=20 par défaut.
     */
    public Page<EnergyReadingResponseDto> getAll(int page, int size) {
        return repository
                .findAllByOrderByReceivedAtDesc(PageRequest.of(page, size))
                .map(this::toResponseDto);
    }

    /**
     * Retourne une mesure par son id.
     */
    public EnergyReadingResponseDto getById(Long id) {
        EnergyReading reading = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reading not found with id: " + id));
        return toResponseDto(reading);
    }

    // ── Appels inter-services ─────────────────────────────────────

    private void forwardToBilling(EnergyReadingDto dto) {
        try {
            restTemplate.postForEntity(
                    billingServiceUrl + "/bills/calculate",
                    dto,
                    Void.class
            );
            log.debug("Forwarded to Billing — meterId={}", dto.getMeterId());
        } catch (RestClientException e) {
            log.warn("Failed to forward to Billing — meterId={}: {}", dto.getMeterId(), e.getMessage());
        }
    }

    private void forwardToAnomaly(EnergyReadingDto dto) {
        try {
            restTemplate.postForEntity(
                    anomalyServiceUrl + "/analyze",
                    dto,
                    Void.class
            );
            log.debug("Forwarded to Anomaly — meterId={}", dto.getMeterId());
        } catch (RestClientException e) {
            log.warn("Failed to forward to Anomaly — meterId={}: {}", dto.getMeterId(), e.getMessage());
        }
    }

    // ── Mapper ────────────────────────────────────────────────────

    private EnergyReadingResponseDto toResponseDto(EnergyReading r) {
        return EnergyReadingResponseDto.builder()
                .id(r.getId())
                .meterId(r.getMeterId())
                .consumption(r.getConsumption())
                .timestamp(r.getTimestamp())
                .receivedAt(r.getReceivedAt())
                .build();
    }
}