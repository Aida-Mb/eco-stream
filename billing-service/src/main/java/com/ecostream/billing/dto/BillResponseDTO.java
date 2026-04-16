package com.ecostream.billing.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
@Data
@Builder
public class BillResponseDTO {
    private Long id;
    private String meterId;
    private Double consumption;     // kWh consommés
    private Double cost;            // coût calculé en FCFA
    private Double rateApplied;     // tarif appliqué (plein ou creux)
    private String tariffType;      // "HEURES_PLEINES" ou "HEURES_CREUSES"
    private LocalDateTime timestamp;
    private LocalDateTime calculatedAt;
}
