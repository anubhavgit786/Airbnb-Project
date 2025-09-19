package com.anubhav.airbnb.services;

import com.anubhav.airbnb.dtos.*;
import com.anubhav.airbnb.enums.BookingStatus;
import com.anubhav.airbnb.exceptions.BadRequestException;
import com.anubhav.airbnb.exceptions.ResourceConflictException;
import com.anubhav.airbnb.exceptions.ResourceNotFoundException;
import com.anubhav.airbnb.exceptions.UnauthorizedException;
import com.anubhav.airbnb.models.*;
import com.anubhav.airbnb.repositories.*;
import com.anubhav.airbnb.strategy.PricingService;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    private final CheckoutService checkoutService;
    private final PricingService pricingService;

    @Value("${frontend.url}")
    private String frontendUrl;

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

        BigDecimal priceForOneRoom = pricingService.calculateTotalPrice(inventoryList);
        BigDecimal totalPrice = priceForOneRoom.multiply(BigDecimal.valueOf(request.getRoomsCount()));

        Booking booking = Booking.createReservedBooking(hotel, room, user, request, totalPrice);
        Booking savedBooking = bookingRepository.save(booking);

        return modelMapper.map(savedBooking, BookingDto.class);
    }

    @Override
    @Transactional
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

    @Override
    @Transactional
    public Map<String, String> initiatePayments(Long bookingId)
    {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new ResourceNotFoundException("Booking not found with id: "+bookingId)
        );

        User user = userService.getCurrentUser();

        if (!user.equals(booking.getUser()))
        {
            throw new UnauthorizedException("Booking does not belong to this user with id: "+user.getId());
        }

        if(booking.hasBookingExpired())
        {
            throw new ResourceConflictException("Booking has expired");
        }

        String sessionUrl = checkoutService.getCheckoutSession(booking,
                frontendUrl+"/payments/" +bookingId +"/status",
                frontendUrl+"/payments/" +bookingId +"/status");

        booking.setBookingStatus(BookingStatus.PAYMENTS_PENDING);
        bookingRepository.save(booking);

        return Map.of("sessionUrl", sessionUrl);
    }

    @Override
    @Transactional
    public void capturePayment(Event event)
    {
        switch (event.getType())
        {
            case "checkout.session.completed" ->
            {
                String sessionId = checkoutService.extractSessionId(event);
                Booking booking =
                        bookingRepository.findByPaymentSessionId(sessionId).orElseThrow(() ->
                                new ResourceNotFoundException("Booking not found for session ID: "+sessionId));

                booking.setBookingStatus(BookingStatus.CONFIRMED);
                bookingRepository.save(booking);
                inventoryRepository.findAndLockReservedInventory(
                        booking.getRoom().getId(),
                        booking.getCheckInDate(),
                        booking.getCheckOutDate(),
                        booking.getRoomsCount());

                inventoryRepository.confirmBooking(
                        booking.getRoom().getId(),
                        booking.getCheckInDate(),
                        booking.getCheckOutDate(),
                        booking.getRoomsCount());
            }

            default ->
            {
                return;
            }
        }
        return;

    }

    @Override
    public BookingStatusResponseDto getBookingStatus(Long bookingId)
    {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new ResourceNotFoundException("Booking not found with id: "+bookingId)
        );

        User user = userService.getCurrentUser();

        if (!user.equals(booking.getUser()))
        {
            throw new UnauthorizedException("Booking does not belong to this user with id: "+user.getId());
        }

        return new BookingStatusResponseDto(booking.getBookingStatus());
    }

    @Override
    public List<BookingDto> getAllBookingsByHotelId(Long hotelId)
    {
        Hotel hotel = hotelRepository.findById(hotelId).orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id: " + hotelId));
        User user = userService.getCurrentUser();

        if (!user.equals(hotel.getOwner()))
        {
            throw new UnauthorizedException("You are not the owner of this hotel with hotel id : " + hotelId);
        }

        List<Booking> bookings = bookingRepository.findByHotel(hotel);

        return bookings
                .stream()
                .map(booking -> modelMapper.map(booking, BookingDto.class))
                .toList();
    }

    @Override
    public HotelReportResponseDto getHotelReport(Long hotelId, HotelReportRequestDto request)
    {
        Hotel hotel = hotelRepository.findById(hotelId).orElseThrow(() -> new ResourceNotFoundException("Hotel not " +
                "found with ID: "+hotelId));

        User user = userService.getCurrentUser();

        if (!user.equals(hotel.getOwner()))
        {
            throw new UnauthorizedException("You are not the owner of this hotel with hotel id : " + hotelId);
        }

        LocalDateTime startDateTime = request.getStartDate().atStartOfDay();
        LocalDateTime endDateTime = request.getEndDate().atTime(LocalTime.MAX);

        List<Booking> bookings = bookingRepository.findByHotelAndCreatedAtBetween(
                hotel,
                startDateTime,
                endDateTime);

        Long totalConfirmedBookings = bookings
                .stream()
                .filter(booking -> booking.getBookingStatus() == BookingStatus.CONFIRMED)
                .count();

        BigDecimal totalRevenueOfConfirmedBookings = bookings.stream()
                .filter(booking -> booking.getBookingStatus() == BookingStatus.CONFIRMED)
                .map(Booking::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal avgRevenue = totalConfirmedBookings == 0 ? BigDecimal.ZERO :
                totalRevenueOfConfirmedBookings.divide(BigDecimal.valueOf(totalConfirmedBookings), RoundingMode.HALF_UP);

        return new HotelReportResponseDto(totalConfirmedBookings, totalRevenueOfConfirmedBookings, avgRevenue);
    }

    @Override
    public List<BookingDto> getMyBookings()
    {
        User user = userService.getCurrentUser();

        return bookingRepository
                .findByUser(user)
                .stream().
                map((b) -> modelMapper.map(b, BookingDto.class))
                .toList();
    }

    @Override
    @Transactional
    public void cancelBooking(Long bookingId)
    {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new ResourceNotFoundException("Booking not found with id: "+bookingId)
        );
        User user = userService.getCurrentUser();

        if (!user.equals(booking.getUser()))
        {
            throw new UnauthorizedException("Booking does not belong to this user with id: "+user.getId());
        }

        if(booking.getBookingStatus() != BookingStatus.CONFIRMED)
        {
            throw new BadRequestException("Only confirmed bookings can be cancelled");
        }

        booking.setBookingStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        inventoryRepository.findAndLockReservedInventory(
                booking.getRoom().getId(),
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                booking.getRoomsCount());

        inventoryRepository.cancelBooking(
                booking.getRoom().getId(),
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                booking.getRoomsCount());

        // handle the refund

        try
        {
            Session session = Session.retrieve(booking.getPaymentSessionId());
            checkoutService.cancelSession(session);
        }
        catch (StripeException e)
        {
            throw new BadRequestException(e.getMessage());
        }
    }
}
