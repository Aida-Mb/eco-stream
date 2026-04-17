package com.ecostream.anomaly.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Payload reçu de l'Ingestion Service via POST /analyze.
 * Même contrat que EnergyReadingDto de l'Ingestion Service.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnergyReadingDto {

    @NotBlank(message = "meterId is required")
    private String meterId;

    @NotNull(message = "consumption is required")
    @Min(value = 0, message = "consumption must be positive")
    private Double consumption; // en kWh

    @NotBlank(message = "timestamp is required")
    private String timestamp;   // ISO-8601
}
