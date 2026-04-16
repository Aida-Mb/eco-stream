package com.ecostream.ingestion.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Payload reçu du Simulator Service via POST /readings.
 * Même structure que EnergyReadingDto du Simulator — contrat partagé.
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