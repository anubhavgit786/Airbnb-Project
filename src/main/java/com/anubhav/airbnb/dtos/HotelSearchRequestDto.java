package com.anubhav.airbnb.dtos;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class HotelSearchRequestDto
{

    @NotBlank(message = "City is required")
    private String city;

    @NotNull(message = "Start date is required")
    @FutureOrPresent(message = "Start date must be today or in the future")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    @Future(message = "End date must be in the future")
    private LocalDate endDate;

    @Min(value = 1, message = "At least 1 room must be requested")
    private Integer roomsCount;

    @Min(value = 0, message = "Page index cannot be negative")
    private Integer page = 0;

    @Min(value = 1, message = "Page size must be at least 1")
    @Max(value = 100, message = "Page size cannot be greater than 100")
    private Integer size = 10;

    /**
     * Custom validation helper method (optional).
     * Can be used in Service/Controller to ensure startDate < endDate.
     */
    public boolean isValidDateRange()
    {
        return startDate != null && endDate != null && !endDate.isBefore(startDate);
    }
}

