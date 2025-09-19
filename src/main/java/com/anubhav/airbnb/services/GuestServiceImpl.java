package com.anubhav.airbnb.services;

import com.anubhav.airbnb.dtos.GuestDto;
import com.anubhav.airbnb.exceptions.UnauthorizedException;
import com.anubhav.airbnb.models.Guest;
import com.anubhav.airbnb.models.User;
import com.anubhav.airbnb.repositories.GuestRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GuestServiceImpl implements GuestService
{
    private final GuestRepository guestRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;

    @Override
    public List<GuestDto> getAllGuests()
    {
        User user = userService.getCurrentUser();
        List<Guest> guests = guestRepository.findByUser(user);
        return guests.stream()
                .map(guest -> modelMapper.map(guest, GuestDto.class))
                .toList();
    }

    @Override
    public void updateGuest(Long guestId, GuestDto guestDto)
    {
        Guest guest = guestRepository.findById(guestId)
                .orElseThrow(() -> new EntityNotFoundException("Guest not found"));

        User user = userService.getCurrentUser();
        if(!user.equals(guest.getUser()))
        {
            throw new UnauthorizedException("You are not the owner of this guest");
        }

        modelMapper.map(guestDto, guest);
        guest.setUser(user);
        guest.setId(guestId);
        guestRepository.save(guest);
    }

    @Override
    public void deleteGuest(Long guestId)
    {
        Guest guest = guestRepository.findById(guestId)
                .orElseThrow(() -> new EntityNotFoundException("Guest not found"));

        User user = userService.getCurrentUser();
        if(!user.equals(guest.getUser()))
        {
            throw new UnauthorizedException("You are not the owner of this guest");
        }

        guestRepository.deleteById(guestId);
    }

    @Override
    public GuestDto addNewGuest(GuestDto guestDto)
    {
        User user = userService.getCurrentUser();
        Guest guest = modelMapper.map(guestDto, Guest.class);
        guest.setUser(user);
        Guest savedGuest = guestRepository.save(guest);
        return modelMapper.map(savedGuest, GuestDto.class);
    }
}
