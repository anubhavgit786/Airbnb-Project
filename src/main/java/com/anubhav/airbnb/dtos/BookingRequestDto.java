package com.anubhav.airbnb.dtos;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class BookingRequestDto
{
    @NotNull(message = "Hotel ID is required")
    @Positive(message = "Hotel ID must be a positive number")
    private Long hotelId;

    @NotNull(message = "Room ID is required")
    @Positive(message = "Room ID must be a positive number")
    private Long roomId;

    @NotNull(message = "Check-in date is required")
    @FutureOrPresent(message = "Check-in date must be today or in the future")
    private LocalDate checkInDate;

    @NotNull(message = "Check-out date is required")
    @Future(message = "Check-out date must be in the future")
    private LocalDate checkOutDate;

    @NotNull(message = "Rooms count is required")
    @Min(value = 1, message = "At least 1 room must be booked")
    private Integer roomsCount;

    public boolean isValidDateRange()
    {
        return checkInDate != null && checkOutDate != null && !checkOutDate.isBefore(checkInDate);
    }
}
