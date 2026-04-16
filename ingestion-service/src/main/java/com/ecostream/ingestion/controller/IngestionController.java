package com.ecostream.ingestion.controller;

import com.ecostream.ingestion.dto.EnergyReadingDto;
import com.ecostream.ingestion.dto.EnergyReadingResponseDto;
import com.ecostream.ingestion.service.IngestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoints REST de l'Ingestion Service.
 *
 *  POST /readings          — reçoit une mesure du Simulator
 *  GET  /readings          — liste toutes les mesures (pagination)
 *  GET  /readings/{id}     — détail d'une mesure
 *  GET  /health            — health check
 */
@RestController
@RequestMapping("/readings")
@RequiredArgsConstructor
public class IngestionController {

    private final IngestionService ingestionService;

    /**
     * Reçoit une mesure depuis le Simulator, valide, persiste
     * et forward vers Billing + Anomaly en parallèle.
     *
     * Body : { "meterId": "house-1", "consumption": 3.42, "timestamp": "..." }
     */
    @PostMapping
    public ResponseEntity<EnergyReadingResponseDto> ingest(
            @Valid @RequestBody EnergyReadingDto dto) {
        EnergyReadingResponseDto response = ingestionService.ingest(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Liste toutes les mesures reçues avec pagination optionnelle.
     * Ex : GET /readings?page=0&size=20
     */
    @GetMapping
    public ResponseEntity<Page<EnergyReadingResponseDto>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ingestionService.getAll(page, size));
    }

    /**
     * Retourne le détail d'une mesure par son id.
     */
    @GetMapping("/{id}")
    public ResponseEntity<EnergyReadingResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ingestionService.getById(id));
    }

    /**
     * Health check simple.
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("{\"status\":\"UP\"}");
    }
}