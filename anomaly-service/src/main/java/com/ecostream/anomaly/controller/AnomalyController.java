package com.ecostream.anomaly.controller;

import com.ecostream.anomaly.dto.AnalysisResultDto;
import com.ecostream.anomaly.dto.AnomalyAlertDto;
import com.ecostream.anomaly.dto.EnergyReadingDto;
import com.ecostream.anomaly.service.AnomalyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class AnomalyController {

    private final AnomalyService anomalyService;

    /**
     * POST /analyze
     * Appelé par l'Ingestion Service après validation d'une mesure.
     * Analyse la consommation et persiste l'alerte si anomalie détectée.
     */
    @PostMapping("/analyze")
    public ResponseEntity<AnalysisResultDto> analyze(
            @Valid @RequestBody EnergyReadingDto reading) {

        log.info("Requête d'analyse reçue — meterId={}", reading.getMeterId());
        AnalysisResultDto result = anomalyService.analyze(reading);
        return ResponseEntity.ok(result);
    }

    /**
     * GET /alerts
     * Retourne toutes les anomalies enregistrées.
     * Optionnel : filtrer par sévérité via ?severity=HIGH|MEDIUM
     */
    @GetMapping("/alerts")
    public ResponseEntity<List<AnomalyAlertDto>> getAllAlerts(
            @RequestParam(required = false) String severity) {

        List<AnomalyAlertDto> alerts = (severity != null && !severity.isBlank())
                ? anomalyService.getAlertsBySeverity(severity)
                : anomalyService.getAllAlerts();

        return ResponseEntity.ok(alerts);
    }

    /**
     * GET /alerts/{id}
     * Retourne une alerte précise par son identifiant.
     */
    @GetMapping("/alerts/{id}")
    public ResponseEntity<AnomalyAlertDto> getAlertById(@PathVariable Long id) {
        return ResponseEntity.ok(anomalyService.getAlertById(id));
    }

    /**
     * GET /alerts/meter/{meterId}
     * Retourne toutes les anomalies d'un compteur donné.
     */
    @GetMapping("/alerts/meter/{meterId}")
    public ResponseEntity<List<AnomalyAlertDto>> getAlertsByMeter(
            @PathVariable String meterId) {

        return ResponseEntity.ok(anomalyService.getAlertsByMeter(meterId));
    }

    /**
     * GET /health
     * Health check standard.
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Anomaly Detection Service UP");
    }
}
