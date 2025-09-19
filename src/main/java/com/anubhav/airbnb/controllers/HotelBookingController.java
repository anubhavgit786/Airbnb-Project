package com.anubhav.airbnb.controllers;

import com.anubhav.airbnb.dtos.BookingDto;
import com.anubhav.airbnb.dtos.BookingRequestDto;
import com.anubhav.airbnb.dtos.BookingStatusResponseDto;
import com.anubhav.airbnb.dtos.GuestDto;
import com.anubhav.airbnb.services.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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


    @PostMapping("/{bookingId}/cancel")
    //@Operation(summary = "Cancel the booking", tags = {"Booking Flow"})
    public ResponseEntity<Void> cancelBooking(@PathVariable Long bookingId)
    {
        bookingService.cancelBooking(bookingId);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/{bookingId}/payments")
    public ResponseEntity<Map<String, String>> initiatePayment(@PathVariable Long bookingId)
    {
        Map<String, String> response = bookingService.initiatePayments(bookingId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{bookingId}/status")
    //@Operation(summary = "Check the status of the booking", tags = {"Booking Flow"})
    public ResponseEntity<BookingStatusResponseDto> getBookingStatus(@PathVariable Long bookingId)
    {
        BookingStatusResponseDto response = bookingService.getBookingStatus(bookingId);
        return ResponseEntity.ok(response);
    }
}
