package com.anubhav.airbnb.services;

import com.anubhav.airbnb.dtos.*;
import com.stripe.model.Event;

import java.util.List;
import java.util.Map;

public interface BookingService
{
    BookingDto initialiseBooking(BookingRequestDto request);
    BookingDto addGuests(Long bookingId, List<GuestDto> guestDtoList);
    Map<String, String> initiatePayments(Long bookingId);
    void cancelBooking(Long bookingId);
    void capturePayment(Event event);
    BookingStatusResponseDto getBookingStatus(Long bookingId);
    List<BookingDto> getAllBookingsByHotelId(Long hotelId);
    HotelReportResponseDto getHotelReport(Long hotelId, HotelReportRequestDto request);
    List<BookingDto> getMyBookings();
}
