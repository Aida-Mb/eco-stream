package com.ecostream.anomaly.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Réponse retournée après analyse d'une mesure.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnomalyAlertDto {

    private Long id;
    private String meterId;
    private Double consumption;       // kWh analysé
    private String severityLevel;     // "HIGH", "MEDIUM", "NORMAL"
    private String message;           // description lisible de l'alerte
    private Double threshold;         // seuil dépassé
    private String timestamp;         // timestamp de la mesure originale
    private LocalDateTime detectedAt; // quand l'anomalie a été détectée
}
