package com.ecostream.billing.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bills")
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String meterId;

    @Column(nullable = false)
    private Double consumption;

    @Column(nullable = false)
    private Double cost;

    @Column(nullable = false)
    private Double rateApplied;

    @Column(nullable = false)
    private String tariffType;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private LocalDateTime calculatedAt;

    // Constructeur vide obligatoire pour JPA
    public Bill() {}

    // Constructeur complet
    public Bill(Long id, String meterId, Double consumption, Double cost,
                Double rateApplied, String tariffType,
                LocalDateTime timestamp, LocalDateTime calculatedAt) {
        this.id = id;
        this.meterId = meterId;
        this.consumption = consumption;
        this.cost = cost;
        this.rateApplied = rateApplied;
        this.tariffType = tariffType;
        this.timestamp = timestamp;
        this.calculatedAt = calculatedAt;
    }

    // Getters
    public Long getId()                  { return id; }
    public String getMeterId()           { return meterId; }
    public Double getConsumption()       { return consumption; }
    public Double getCost()              { return cost; }
    public Double getRateApplied()       { return rateApplied; }
    public String getTariffType()        { return tariffType; }
    public LocalDateTime getTimestamp()  { return timestamp; }
    public LocalDateTime getCalculatedAt() { return calculatedAt; }

    // Setters
    public void setId(Long id)                        { this.id = id; }
    public void setMeterId(String meterId)            { this.meterId = meterId; }
    public void setConsumption(Double consumption)    { this.consumption = consumption; }
    public void setCost(Double cost)                  { this.cost = cost; }
    public void setRateApplied(Double rateApplied)    { this.rateApplied = rateApplied; }
    public void setTariffType(String tariffType)      { this.tariffType = tariffType; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public void setCalculatedAt(LocalDateTime calculatedAt) { this.calculatedAt = calculatedAt; }

    // Builder statique (remplace @Builder de Lombok)
    public static BillBuilder builder() { return new BillBuilder(); }

    public static class BillBuilder {
        private Long id;
        private String meterId;
        private Double consumption;
        private Double cost;
        private Double rateApplied;
        private String tariffType;
        private LocalDateTime timestamp;
        private LocalDateTime calculatedAt;

        public BillBuilder id(Long id)                        { this.id = id; return this; }
        public BillBuilder meterId(String meterId)            { this.meterId = meterId; return this; }
        public BillBuilder consumption(Double consumption)    { this.consumption = consumption; return this; }
        public BillBuilder cost(Double cost)                  { this.cost = cost; return this; }
        public BillBuilder rateApplied(Double rateApplied)    { this.rateApplied = rateApplied; return this; }
        public BillBuilder tariffType(String tariffType)      { this.tariffType = tariffType; return this; }
        public BillBuilder timestamp(LocalDateTime timestamp) { this.timestamp = timestamp; return this; }
        public BillBuilder calculatedAt(LocalDateTime t)      { this.calculatedAt = t; return this; }

        public Bill build() {
            return new Bill(id, meterId, consumption, cost,
                    rateApplied, tariffType, timestamp, calculatedAt);
        }
    }
}