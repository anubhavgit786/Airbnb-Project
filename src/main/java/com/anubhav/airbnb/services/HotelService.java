package com.anubhav.airbnb.services;

import com.anubhav.airbnb.dtos.HotelDto;
import com.anubhav.airbnb.dtos.HotelInfoDto;

import java.util.List;

public interface HotelService
{
    HotelDto getHotelById(Long hotelId);
    HotelDto createNewHotel(HotelDto inputHotel);
    HotelDto updateHotel(Long hotelId, HotelDto inputHotel);
    void deleteHotel(Long hotelId);
    void activateHotel(Long hotelId);
    HotelInfoDto getHotelInfo(Long hotelId);
    List<HotelDto> getAllHotels();
}
