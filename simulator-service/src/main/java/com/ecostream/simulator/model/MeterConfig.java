package com.ecostream.simulator.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Représente la configuration d'un compteur intelligent simulé.
 * Chaque compteur a un identifiant unique et un intervalle d'envoi.
 */
@Entity
@Table(name = "meter_config")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeterConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "meter_id", unique = true, nullable = false, length = 50)
    private String meterId;

    @Column(name = "interval_ms", nullable = false)
    private int intervalMs;

    @Column(nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
