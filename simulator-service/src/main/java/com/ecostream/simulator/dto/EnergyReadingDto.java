package com.ecostream.simulator.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Payload envoyé à POST /readings de l'Ingestion Service.
 * Ce DTO constitue le contrat de communication entre Simulator et Ingestion.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnergyReadingDto {

    private String meterId;
    private double consumption;  // en kWh
    private String timestamp;    // ISO-8601, ex: "2025-04-15T14:30:00Z"
}
