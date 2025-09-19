package com.anubhav.airbnb.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class InventoryResponseDto
{
    private Long id;

    @NotNull(message = "Date is required")
    private LocalDate date;

    @Min(value = 0, message = "Booked count cannot be negative")
    private Integer bookedCount;

    @Min(value = 0, message = "Reserved count cannot be negative")
    private Integer reservedCount;

    @NotNull(message = "Total count is required")
    @Min(value = 1, message = "Total count must be at least 1")
    private Integer totalCount;

    @NotNull(message = "Surge factor is required")
    @DecimalMin(value = "0.00", inclusive = false, message = "Surge factor must be greater than 0")
    @Digits(integer = 3, fraction = 2, message = "Surge factor can have up to 3 digits and 2 decimals")
    private BigDecimal surgeFactor;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.00", inclusive = false, message = "Price must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Price can have up to 8 digits and 2 decimals")
    private BigDecimal price;

    @NotNull(message = "Closed flag is required")
    private Boolean closed;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}