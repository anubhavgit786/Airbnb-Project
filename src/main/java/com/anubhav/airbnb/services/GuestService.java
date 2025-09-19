package com.anubhav.airbnb.services;

import com.anubhav.airbnb.dtos.GuestDto;

import java.util.List;

public interface GuestService
{
    List<GuestDto> getAllGuests();

    void updateGuest(Long guestId, GuestDto guestDto);

    void deleteGuest(Long guestId);

    GuestDto addNewGuest(GuestDto guestDto);
}
