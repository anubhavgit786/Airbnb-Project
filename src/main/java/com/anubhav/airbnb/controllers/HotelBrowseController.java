package com.anubhav.airbnb.controllers;


import com.anubhav.airbnb.dtos.HotelDto;
import com.anubhav.airbnb.dtos.HotelInfoDto;
import com.anubhav.airbnb.dtos.HotelPriceDto;
import com.anubhav.airbnb.dtos.HotelSearchRequestDto;
import com.anubhav.airbnb.services.HotelService;
import com.anubhav.airbnb.services.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/hotels")
@RequiredArgsConstructor
public class HotelBrowseController
{

    private final InventoryService inventoryService;
    private final HotelService hotelService;

    @GetMapping("/search")
    public ResponseEntity<List<HotelPriceDto>> searchHotels(@Valid @RequestBody HotelSearchRequestDto hotelSearchRequest)
    {
        List<HotelPriceDto> response = inventoryService.searchHotels(hotelSearchRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{hotelId}/info")
    public ResponseEntity<HotelInfoDto> getHotelInfo(@PathVariable Long hotelId)
    {
        HotelInfoDto response = hotelService.getHotelInfo(hotelId);
        return ResponseEntity.ok(response);
    }
}
