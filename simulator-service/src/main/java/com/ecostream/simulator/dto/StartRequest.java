package com.ecostream.simulator.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Body de POST /start
 */
@Data
public class StartRequest {

    @NotNull(message = "meterCount is required")
    @Min(value = 1, message = "meterCount must be at least 1")
    private Integer meterCount;

    @NotNull(message = "intervalMs is required")
    @Min(value = 500, message = "intervalMs must be at least 500ms")
    private Integer intervalMs;
}
