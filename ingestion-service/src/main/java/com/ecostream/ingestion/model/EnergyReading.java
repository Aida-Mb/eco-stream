package com.ecostream.ingestion.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * Entité persistée en base pour chaque mesure reçue du Simulator.
 */
@Entity
@Table(name = "energy_reading")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnergyReading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "meter_id", nullable = false, length = 50)
    private String meterId;

    @Column(nullable = false)
    private double consumption; // en kWh

    @Column(nullable = false)
    private String timestamp;   // ISO-8601

    @Column(name = "received_at", nullable = false)
    private Instant receivedAt;

    @PrePersist
    public void prePersist() {
        this.receivedAt = Instant.now();
    }
}