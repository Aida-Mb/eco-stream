package com.ecostream.ingestion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Réponse retournée par GET /readings et GET /readings/{id}
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnergyReadingResponseDto {

    private Long id;
    private String meterId;
    private double consumption;
    private String timestamp;
    private Instant receivedAt;
}