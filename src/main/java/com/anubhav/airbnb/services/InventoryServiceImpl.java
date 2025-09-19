package com.anubhav.airbnb.services;

import com.anubhav.airbnb.dtos.*;
import com.anubhav.airbnb.exceptions.BadRequestException;
import com.anubhav.airbnb.exceptions.ResourceNotFoundException;
import com.anubhav.airbnb.exceptions.UnauthorizedException;
import com.anubhav.airbnb.models.Hotel;
import com.anubhav.airbnb.models.Inventory;
import com.anubhav.airbnb.models.Room;
import com.anubhav.airbnb.models.User;
import com.anubhav.airbnb.repositories.HotelMinPriceRepository;
import com.anubhav.airbnb.repositories.HotelRepository;
import com.anubhav.airbnb.repositories.InventoryRepository;
import com.anubhav.airbnb.repositories.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.temporal.ChronoUnit;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService
{

    private final InventoryRepository inventoryRepository;
    private final RoomRepository roomRepository;
    private final UserService userService;
    private final HotelRepository hotelRepository;
    private final ModelMapper modelMapper;
    private final HotelMinPriceRepository hotelMinPriceRepository;

    @Async
    @Override
    public void initializeRoomForAYear(Room room)
    {
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusYears(1);
        List<Inventory> inventories = new ArrayList<>();

        while (!today.isAfter(endDate))
        {
            Inventory inventory = Inventory.createInventory(room, today);
            inventories.add(inventory);
            today=today.plusDays(1);
        }

        inventoryRepository.saveAll(inventories); //Batch insert
    }

    @Override
    public List<HotelPriceDto> searchHotels(HotelSearchRequestDto request)
    {
        if (!request.isValidDateRange())
        {
            throw new BadRequestException("End date must be after or equal to start date");
        }

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());

        long daysBetween = ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate()) + 1;


//        Page<Hotel> hotels = inventoryRepository.findAvailableHotels(
//                request.getCity(),
//                request.getStartDate(),
//                request.getEndDate(),
//                request.getRoomsCount(),
//                daysBetween,
//                pageable
//        );

        Page<HotelPriceDto> hotels = hotelMinPriceRepository.findHotelsWithAvailableInventory(
                request.getCity(),
                request.getStartDate(),
                request.getEndDate(),
                request.getRoomsCount(),
                daysBetween,
                pageable);

//        return hotels
//                .getContent()
//                .stream()
//                .map(hotel -> modelMapper.map(hotel, HotelDto.class))
//                .toList();

        return hotels
                .getContent();
    }

    @Override
    public List<InventoryResponseDto> getAllInventoryByRoom(Long roomId)
    {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: "+roomId));

        User user = userService.getCurrentUser();

        if(!user.equals(room.getHotel().getOwner()))
        {
            throw new UnauthorizedException("You are not the owner of room with id: "+roomId);
        }

        return inventoryRepository
                .findByRoomOrderByDate(room)
                .stream()
                .map((i) -> modelMapper.map(i, InventoryResponseDto.class))
                .toList();
    }

    @Override
    @Transactional
    public void updateInventory(Long roomId, UpdateInventoryRequestDto request)
    {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: "+roomId));

        User user = userService.getCurrentUser();

        if(!user.equals(room.getHotel().getOwner()))
        {
            throw new UnauthorizedException("You are not the owner of room with id: " + roomId);
        }

        inventoryRepository.getInventoryAndLockBeforeUpdate(
                roomId,
                request.getStartDate(),
                request.getEndDate());

        inventoryRepository.updateInventory(
                roomId,
                request.getStartDate(),
                request.getEndDate(),
                request.getClosed(),
                request.getSurgeFactor());

    }
}
