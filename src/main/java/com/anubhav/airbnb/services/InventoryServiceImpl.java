package com.anubhav.airbnb.services;

import com.anubhav.airbnb.dtos.HotelDto;
import com.anubhav.airbnb.dtos.HotelPriceDto;
import com.anubhav.airbnb.dtos.HotelSearchRequestDto;
import com.anubhav.airbnb.exceptions.BadRequestException;
import com.anubhav.airbnb.models.Hotel;
import com.anubhav.airbnb.models.Inventory;
import com.anubhav.airbnb.models.Room;
import com.anubhav.airbnb.repositories.HotelMinPriceRepository;
import com.anubhav.airbnb.repositories.HotelRepository;
import com.anubhav.airbnb.repositories.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService
{

    private final InventoryRepository inventoryRepository;
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
}
