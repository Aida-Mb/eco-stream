package com.ecostream.billing.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class EnergyReadingDTO {
    @NotBlank(message= "L'identifiant du compteur est obligatoire")
    private String meterId;

    @NotNull
    @Positive(message="La consommation doit etre positive")
    private Double consumption;

    @NotNull
    private LocalDateTime timestamp;
}

