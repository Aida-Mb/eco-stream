package com.ecostream.billing.model;

import lombok.AllArgsConstructor;
import lombok.Data;
// Pas d'annotation @Entity — c'est juste un objet de réponse
@Data
@AllArgsConstructor
public class Tariff {
    private Double peakRate;
    private Double offPeakRate;
    private int peakStartHour;
    private int peakEndHour;
}
