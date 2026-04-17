package com.ecostream.anomaly.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entité persistée pour chaque anomalie détectée.
 */
@Entity
@Table(name = "anomaly_alerts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnomalyAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String meterId;

    @Column(nullable = false)
    private Double consumption;

    /** "HIGH", "MEDIUM" */
    @Column(nullable = false)
    private String severityLevel;

    @Column(nullable = false)
    private String message;

    /** Seuil qui a été dépassé */
    @Column(nullable = false)
    private Double threshold;

    /** Timestamp ISO-8601 de la mesure originale */
    @Column(nullable = false)
    private String timestamp;

    /** Moment où l'anomalie a été enregistrée */
    @Column(nullable = false)
    private LocalDateTime detectedAt;
}
