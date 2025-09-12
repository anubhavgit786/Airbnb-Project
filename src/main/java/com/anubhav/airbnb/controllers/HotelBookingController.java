package com.anubhav.airbnb.controllers;

import com.anubhav.airbnb.dtos.BookingDto;
import com.anubhav.airbnb.dtos.BookingRequestDto;
import com.anubhav.airbnb.dtos.GuestDto;
import com.anubhav.airbnb.services.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bookings")
public class HotelBookingController
{

    private final BookingService bookingService;

    @PostMapping("/init")
    public ResponseEntity<BookingDto> initialiseBooking(@RequestBody BookingRequestDto request)
    {
        BookingDto response = bookingService.initialiseBooking(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{bookingId}/addGuests")
    public ResponseEntity<BookingDto> addGuests(@PathVariable Long bookingId, @RequestBody List<GuestDto> guestDtoList)
    {
        BookingDto response = bookingService.addGuests(bookingId, guestDtoList);
        return ResponseEntity.ok(response);
    }
}
