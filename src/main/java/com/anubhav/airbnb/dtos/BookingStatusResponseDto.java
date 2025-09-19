package com.anubhav.airbnb.dtos;

import com.anubhav.airbnb.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingStatusResponseDto
{
    private BookingStatus bookingStatus;
}