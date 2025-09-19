package com.anubhav.airbnb.controllers;

import com.anubhav.airbnb.dtos.BookingDto;
import com.anubhav.airbnb.dtos.HotelDto;
import com.anubhav.airbnb.dtos.HotelReportRequestDto;
import com.anubhav.airbnb.dtos.HotelReportResponseDto;
import com.anubhav.airbnb.services.BookingService;
import com.anubhav.airbnb.services.HotelService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/hotels")
public class HotelController
{
    private final HotelService hotelService;
    private final BookingService bookingService;

    @GetMapping("/{hotelId}")
    @Operation(summary = "Get a hotel by Id", tags = {"Admin Hotel"})
    public ResponseEntity<HotelDto> getHotelById(@PathVariable Long hotelId)
    {
        HotelDto hotelDto = hotelService.getHotelById(hotelId);
        return ResponseEntity.ok(hotelDto);
    }

    @PostMapping
    @Operation(summary = "Create a new hotel", tags = {"Admin Hotel"})
    public ResponseEntity<HotelDto> createNewHotel(@Valid @RequestBody HotelDto hotelDto)
    {
        HotelDto savedHotelDto = hotelService.createNewHotel(hotelDto);
        return new ResponseEntity<>(savedHotelDto, HttpStatus.CREATED);
    }

    @PutMapping("/{hotelId}")
    @Operation(summary = "Update a hotel", tags = {"Admin Hotel"})
    public ResponseEntity<HotelDto> updateHotel(@PathVariable Long hotelId, @Valid @RequestBody HotelDto hotelDto)
    {
        HotelDto updatedHotelDto = hotelService.updateHotel(hotelId, hotelDto);
        return ResponseEntity.ok(updatedHotelDto);
    }

    @DeleteMapping("/{hotelId}")
    @Operation(summary = "Delete a hotel", tags = {"Admin Hotel"})
    public ResponseEntity<Boolean> deleteHotel(@PathVariable Long hotelId)
    {
        hotelService.deleteHotel(hotelId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{hotelId}")
    @Operation(summary = "Activate a hotel", tags = {"Admin Hotel"})
    public ResponseEntity<HotelDto> activateHotel(@PathVariable Long hotelId)
    {
        hotelService.activateHotel(hotelId);
        return ResponseEntity.noContent().build();
    }


    @GetMapping
    @Operation(summary = "Get all hotels owned by admin", tags = {"Admin Hotel"})
    public ResponseEntity<List<HotelDto>> getAllHotels()
    {
        List<HotelDto> response = hotelService.getAllHotels();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{hotelId}/bookings")
    @Operation(summary = "Get all bookings of a hotel", tags = {"Admin Bookings"})
    public ResponseEntity<List<BookingDto>> getAllBookingsByHotelId(@PathVariable Long hotelId)
    {
        List<BookingDto> response = bookingService.getAllBookingsByHotelId(hotelId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{hotelId}/reports")
    @Operation(summary = "Generate a bookings report of a hotel", tags = {"Admin Bookings"})
    public ResponseEntity<HotelReportResponseDto> getHotelReport(@PathVariable Long hotelId, @RequestBody HotelReportRequestDto request)
    {
        HotelReportResponseDto response = bookingService.getHotelReport(hotelId, request);
        return ResponseEntity.ok(response);
    }



}
