package com.ecostream.simulator.controller;

import com.ecostream.simulator.dto.SimulatorStatusDto;
import com.ecostream.simulator.dto.StartRequest;
import com.ecostream.simulator.service.SimulatorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoints REST du Simulator Service.
 *
 *  POST /start   — démarre la simulation
 *  POST /stop    — arrête la simulation
 *  GET  /status  — état courant
 *  GET  /health  — health check basique (Actuator expose aussi /actuator/health)
 */
@RestController
@RequiredArgsConstructor
public class SimulatorController {

    private final SimulatorService simulatorService;

    /**
     * Démarre la simulation.
     *
     * Body : { "meterCount": 5, "intervalMs": 2000 }
     * Réponse 200 : { "status": "RUNNING", "meterCount": 5, ... }
     */
    @PostMapping("/start")
    public ResponseEntity<SimulatorStatusDto> start(@Valid @RequestBody StartRequest request) {
        SimulatorStatusDto status = simulatorService.start(request);
        return ResponseEntity.ok(status);
    }

    /**
     * Arrête la simulation.
     *
     * Réponse 200 : { "status": "STOPPED", ... }
     */
    @PostMapping("/stop")
    public ResponseEntity<SimulatorStatusDto> stop() {
        SimulatorStatusDto status = simulatorService.stop();
        return ResponseEntity.ok(status);
    }

    /**
     * Retourne l'état courant du simulateur.
     *
     * Réponse 200 : { "status": "RUNNING|STOPPED", "meterCount": 5,
     *                 "intervalMs": 2000, "readingsSent": 42 }
     */
    @GetMapping("/status")
    public ResponseEntity<SimulatorStatusDto> getStatus() {
        return ResponseEntity.ok(simulatorService.getStatus());
    }

    /**
     * Health check simple — l'Actuator expose aussi /actuator/health
     * avec plus de détails (BDD, mémoire, etc.)
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("{\"status\":\"UP\"}");
    }
}
