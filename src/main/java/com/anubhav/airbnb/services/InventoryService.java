package com.anubhav.airbnb.services;

import com.anubhav.airbnb.dtos.HotelDto;
import com.anubhav.airbnb.dtos.HotelPriceDto;
import com.anubhav.airbnb.dtos.HotelSearchRequestDto;
import com.anubhav.airbnb.models.Room;

import java.util.List;

public interface InventoryService
{
    void initializeRoomForAYear(Room room);
    List<HotelPriceDto> searchHotels(HotelSearchRequestDto hotelSearchRequest);
}
