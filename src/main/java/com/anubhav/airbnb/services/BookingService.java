package com.anubhav.airbnb.services;

import com.anubhav.airbnb.dtos.BookingDto;
import com.anubhav.airbnb.dtos.BookingRequestDto;
import com.anubhav.airbnb.dtos.GuestDto;

import java.util.List;

public interface BookingService
{
    BookingDto initialiseBooking(BookingRequestDto request);
    BookingDto addGuests(Long bookingId, List<GuestDto> guestDtoList);
}
