package com.anubhav.airbnb.controllers;


import com.anubhav.airbnb.dtos.HotelDto;
import com.anubhav.airbnb.dtos.HotelInfoDto;
import com.anubhav.airbnb.dtos.HotelPriceDto;
import com.anubhav.airbnb.dtos.HotelSearchRequestDto;
import com.anubhav.airbnb.services.HotelService;
import com.anubhav.airbnb.services.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
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
    @Operation(summary = "Search hotels", tags = {"Browse Hotels"})
    public ResponseEntity<List<HotelPriceDto>> searchHotels(@Valid @RequestBody HotelSearchRequestDto hotelSearchRequest)
    {
        List<HotelPriceDto> response = inventoryService.searchHotels(hotelSearchRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{hotelId}/info")
    @Operation(summary = "Get a hotel info by hotelId", tags = {"Browse Hotels"})
    public ResponseEntity<HotelInfoDto> getHotelInfo(@PathVariable Long hotelId)
    {
        HotelInfoDto response = hotelService.getHotelInfo(hotelId);
        return ResponseEntity.ok(response);
    }
}
