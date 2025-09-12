package com.anubhav.airbnb.controllers;

import com.anubhav.airbnb.dtos.HotelDto;
import com.anubhav.airbnb.services.HotelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/hotels")
public class HotelController
{
    private final HotelService hotelService;

    @GetMapping("/{hotelId}")
    public ResponseEntity<HotelDto> getHotelById(@PathVariable Long hotelId)
    {
        HotelDto hotelDto = hotelService.getHotelById(hotelId);
        return ResponseEntity.ok(hotelDto);
    }

    @PostMapping
    public ResponseEntity<HotelDto> createNewHotel(@Valid @RequestBody HotelDto hotelDto)
    {
        HotelDto savedHotelDto = hotelService.createNewHotel(hotelDto);
        return new ResponseEntity<>(savedHotelDto, HttpStatus.CREATED);
    }

    @PutMapping("/{hotelId}")
    public ResponseEntity<HotelDto> updateHotel(@PathVariable Long hotelId, @Valid @RequestBody HotelDto hotelDto)
    {
        HotelDto updatedHotelDto = hotelService.updateHotel(hotelId, hotelDto);
        return ResponseEntity.ok(updatedHotelDto);
    }

    @DeleteMapping("/{hotelId}")
    public ResponseEntity<Boolean> deleteHotel(@PathVariable Long hotelId)
    {
        hotelService.deleteHotel(hotelId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{hotelId}")
    public ResponseEntity<HotelDto> activateHotel(@PathVariable Long hotelId)
    {
        hotelService.activateHotel(hotelId);
        return ResponseEntity.noContent().build();
    }


}
