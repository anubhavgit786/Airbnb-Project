package com.anubhav.airbnb.services;

import com.anubhav.airbnb.dtos.HotelDto;
import com.anubhav.airbnb.dtos.HotelInfoDto;
import com.anubhav.airbnb.dtos.RoomDto;
import com.anubhav.airbnb.exceptions.ResourceNotFoundException;
import com.anubhav.airbnb.models.Hotel;
import com.anubhav.airbnb.models.Room;
import com.anubhav.airbnb.repositories.HotelRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HotelServiceImpl implements HotelService
{
    private final InventoryService inventoryService;
    private final HotelRepository hotelRepository;
    private final ModelMapper modelMapper;

    @Override
    public HotelDto getHotelById(Long hotelId)
    {
        Hotel hotel = hotelRepository.findById(hotelId).orElseThrow(()-> new ResourceNotFoundException("Hotel not found with id: " + hotelId));
        return modelMapper.map(hotel, HotelDto.class);
    }

    @Override
    public HotelDto createNewHotel(HotelDto inputHotel)
    {
        Hotel hotel = modelMapper.map(inputHotel, Hotel.class);
        hotel.setActive(false);
        Hotel savedHotel = hotelRepository.save(hotel);
        return modelMapper.map(savedHotel, HotelDto.class);
    }

    @Override
    public HotelDto updateHotel(Long hotelId, HotelDto inputHotel)
    {
        Hotel hotel = hotelRepository.findById(hotelId).orElseThrow(()-> new ResourceNotFoundException("Hotel not found with id: " + hotelId));
        modelMapper.map(inputHotel, hotel);
        hotel.setId(hotelId);
        Hotel savedHotel = hotelRepository.save(hotel);
        return modelMapper.map(savedHotel, HotelDto.class);
    }

    @Override
    @Transactional
    public void deleteHotel(Long hotelId)
    {
        Hotel hotel = hotelRepository.findById(hotelId).orElseThrow(()-> new ResourceNotFoundException("Hotel not found with id: " + hotelId));
        hotelRepository.delete(hotel);
    }

    @Transactional
    @Override
    public void activateHotel(Long hotelId)
    {
        Hotel hotel = hotelRepository.findById(hotelId).orElseThrow(()-> new ResourceNotFoundException("Hotel not found with id: " + hotelId));
        hotel.setActive(true);

        for (Room room : hotel.getRooms())
        {
            inventoryService.initializeRoomForAYear(room);
        }

        hotelRepository.save(hotel);
    }

    @Override
    public HotelInfoDto getHotelInfo(Long hotelId)
    {
        Hotel hotel = hotelRepository.findById(hotelId).orElseThrow(()-> new ResourceNotFoundException("Hotel not found with id: " + hotelId));
        List<RoomDto> rooms = hotel.getRooms()
                .stream()
                .map(room -> modelMapper.map(room, RoomDto.class))
                .toList();
        HotelDto hotelDto = modelMapper.map(hotel, HotelDto.class);
        return new HotelInfoDto(hotelDto, rooms);
    }
}
