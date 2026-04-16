package com.ecostream.simulator.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Réponse de GET /status et POST /start
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimulatorStatusDto {

    private String status;      // "RUNNING" ou "STOPPED"
    private int meterCount;
    private int intervalMs;
    private int readingsSent;   // nombre total de relevés envoyés depuis le dernier /start
}
