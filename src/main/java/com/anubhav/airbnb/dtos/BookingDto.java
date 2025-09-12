package com.anubhav.airbnb.dtos;

import com.anubhav.airbnb.enums.BookingStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
public class BookingDto
{
    private Long id;

    @NotNull(message = "Rooms count is required")
    @Min(value = 1, message = "At least 1 room must be booked")
    private Integer roomsCount;

    @NotNull(message = "Check-in date is required")
    @FutureOrPresent(message = "Check-in date must be today or in the future")
    private LocalDate checkInDate;

    @NotNull(message = "Check-out date is required")
    @Future(message = "Check-out date must be in the future")
    private LocalDate checkOutDate;

    private LocalDateTime createdAt; // system generated, read-only
    private LocalDateTime updatedAt; // system generated, read-only

    @NotNull(message = "Booking status is required")
    private BookingStatus bookingStatus;

    // Guests can be empty, but if present they must be valid
    private Set<@Valid GuestDto> guests;
}
