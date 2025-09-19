package com.anubhav.airbnb.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class UpdateInventoryRequestDto
{
    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @NotNull(message = "Surge factor is required")
    @DecimalMin(value = "0.00", inclusive = false, message = "Surge factor must be greater than 0")
    @Digits(integer = 3, fraction = 2, message = "Surge factor can have up to 3 digits and 2 decimals")
    private BigDecimal surgeFactor;

    @NotNull(message = "Closed flag is required")
    private Boolean closed;
}