package com.anubhav.airbnb.services;

import com.anubhav.airbnb.dtos.*;
import com.anubhav.airbnb.models.Room;
import jakarta.validation.Valid;

import java.util.List;

public interface InventoryService
{
    void initializeRoomForAYear(Room room);
    List<HotelPriceDto> searchHotels(HotelSearchRequestDto hotelSearchRequest);
    List<InventoryResponseDto> getAllInventoryByRoom(Long roomId);
    void updateInventory(Long roomId, @Valid UpdateInventoryRequestDto request);
}
