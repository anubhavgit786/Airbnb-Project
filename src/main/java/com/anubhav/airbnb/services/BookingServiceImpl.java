package com.anubhav.airbnb.services;

import com.anubhav.airbnb.dtos.BookingDto;
import com.anubhav.airbnb.dtos.BookingRequestDto;
import com.anubhav.airbnb.dtos.GuestDto;
import com.anubhav.airbnb.enums.BookingStatus;
import com.anubhav.airbnb.exceptions.BadRequestException;
import com.anubhav.airbnb.exceptions.ResourceConflictException;
import com.anubhav.airbnb.exceptions.ResourceNotFoundException;
import com.anubhav.airbnb.models.*;
import com.anubhav.airbnb.repositories.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService
{
    private final GuestRepository guestRepository;
    private final ModelMapper modelMapper;

    private final BookingRepository bookingRepository;
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final InventoryRepository inventoryRepository;
    private final UserService userService;

    @Override
    @Transactional
    public BookingDto initialiseBooking(BookingRequestDto request)
    {
        if (!request.isValidDateRange())
        {
            throw new BadRequestException("Check in date must be before check out date");
        }


        Room room = roomRepository.findById(request.getRoomId()).orElseThrow(() ->  new BadRequestException("Room not found with id: "+ request.getRoomId()));
        Hotel hotel = room.getHotel();

        // Reserve the room/ update the booked count of inventories

        List<Inventory> inventoryList = inventoryRepository.findAndLockAvailableInventory(
                room.getId(),
                request.getCheckInDate(),
                request.getCheckOutDate(),
                request.getRoomsCount()
        );


        int daysCount = (int) ChronoUnit.DAYS.between(request.getCheckInDate(), request.getCheckOutDate())+1;

        if(inventoryList.size() != daysCount)
        {
            throw new ResourceConflictException("Not enough inventory available for the given date range");
        }

        inventoryList
                .forEach(i -> i.setReservedCount(i.getReservedCount() + request.getRoomsCount()));

        //Redundant Query
        //inventoryRepository.saveAll(inventoryList);

        User user = userService.getCurrentUser();

        Booking booking = Booking.createReservedBooking(hotel, room, user, request, BigDecimal.TEN);
        Booking savedBooking = bookingRepository.save(booking);

        return modelMapper.map(savedBooking, BookingDto.class);
    }

    @Override
    public BookingDto addGuests(Long bookingId, List<GuestDto> guestDtoList)
    {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));
        if(booking.hasBookingExpired())
        {
            throw new ResourceConflictException("Booking has expired");
        }

        if (booking.getBookingStatus() != BookingStatus.RESERVED)
        {
            throw new ResourceConflictException("Booking is not under reserved state, cannot add guests");
        }

        User user = userService.getCurrentUser();

        List<Guest> guests = guestDtoList
                .stream()
                .map(dto ->
                {
                    Guest guest = modelMapper.map(dto, Guest.class);
                    guest.setUser(user);
                    return guest;
                })
                .toList();

        List<Guest> savedGuests = guestRepository.saveAll(guests);
        booking.getGuests().addAll(savedGuests);
        booking.setBookingStatus(BookingStatus.GUESTS_ADDED);
        booking = bookingRepository.save(booking);

        return modelMapper.map(booking, BookingDto.class);
    }
}
