package com.anubhav.airbnb.services;

import com.anubhav.airbnb.dtos.RoomDto;
import java.util.List;

public interface RoomService
{
    RoomDto createNewRoomInHotel(Long hotelId, RoomDto roomDto);
    List<RoomDto> getAllRoomsInHotel(Long hotelId);
    RoomDto getRoomById(Long roomId);
    void deleteRoom(Long roomId);
    RoomDto updateRoomById(Long hotelId, Long roomId, RoomDto roomDto);
}
