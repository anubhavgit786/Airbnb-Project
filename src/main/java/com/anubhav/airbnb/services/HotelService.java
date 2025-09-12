package com.anubhav.airbnb.services;

import com.anubhav.airbnb.dtos.HotelDto;
import com.anubhav.airbnb.dtos.HotelInfoDto;

public interface HotelService
{
    HotelDto getHotelById(Long hotelId);
    HotelDto createNewHotel(HotelDto inputHotel);
    HotelDto updateHotel(Long hotelId, HotelDto inputHotel);
    void deleteHotel(Long hotelId);
    void activateHotel(Long hotelId);
    HotelInfoDto getHotelInfo(Long hotelId);
}
