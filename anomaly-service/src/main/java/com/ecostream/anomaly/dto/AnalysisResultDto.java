package com.ecostream.anomaly.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Réponse immédiate retournée à l'Ingestion Service après analyse.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisResultDto {

    private String meterId;
    private Double consumption;
    private String severityLevel;   // "HIGH", "MEDIUM", "NORMAL"
    private boolean anomalyDetected;
    private String message;
}
