package com.anubhav.airbnb.controllers;

import com.anubhav.airbnb.dtos.*;
import com.anubhav.airbnb.services.BookingService;
import com.anubhav.airbnb.services.GuestService;
import com.anubhav.airbnb.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController
{
    private final UserService userService;
    private final GuestService guestService;
    private final BookingService bookingService;

    @PutMapping("/profile")
    @Operation(summary = "Update the user profile", tags = {"Profile"})
    public ResponseEntity<UserDto> updateUser(@Valid @RequestBody UserUpdateDto request, HttpServletResponse response)
    {
        UserDto user = userService.updateUser(request, response);
        return ResponseEntity.ok(user);
    }

    @PatchMapping("/change-password")
    @Operation(summary = "Change the user password", tags = {"Profile"})
    public  ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequestDto request)
    {
        userService.changePassword(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    @Operation(summary = "Get my Profile", tags = {"Profile"})
    public ResponseEntity<UserDto> me()
    {
        UserDto user = userService.me();
        return ResponseEntity.ok(user);
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout user profile", tags = {"Profile"})
    public ResponseEntity<Void> logout(@CookieValue(value = "refreshToken") String refreshToken, HttpServletResponse response)
    {
        userService.logout(refreshToken, response);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/myBookings")
    @Operation(summary = "Get all my previous bookings", tags = {"Profile"})
    public ResponseEntity<List<BookingDto>> getMyBookings()
    {
        List<BookingDto> bookings = bookingService.getMyBookings();
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/profile")
    @Operation(summary = "Get my Profile", tags = {"Profile"})
    public ResponseEntity<UserDto> getMyProfile()
    {
        UserDto userDto = userService.getMyProfile();
        return ResponseEntity.ok(userDto);
    }

    @GetMapping("/guests")
    @Operation(summary = "Get all my guests", tags = {"Booking Guests"})
    public ResponseEntity<List<GuestDto>> getAllGuests()
    {
        List<GuestDto> guests = guestService.getAllGuests();
        return ResponseEntity.ok(guests);
    }

    @PostMapping("/guests")
    @Operation(summary = "Add a new guest to my guests list", tags = {"Booking Guests"})
    public ResponseEntity<GuestDto> addNewGuest( @Valid @RequestBody GuestDto guestDto)
    {
        GuestDto guest = guestService.addNewGuest(guestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(guest);
    }

    @PutMapping("guests/{guestId}")
    @Operation(summary = "Update a guest", tags = {"Booking Guests"})
    public ResponseEntity<Void> updateGuest(@PathVariable Long guestId, @Valid @RequestBody GuestDto guestDto)
    {
        guestService.updateGuest(guestId, guestDto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("guests/{guestId}")
    @Operation(summary = "Remove a guest", tags = {"Booking Guests"})
    public ResponseEntity<Void> deleteGuest(@PathVariable Long guestId)
    {
        guestService.deleteGuest(guestId);
        return ResponseEntity.noContent().build();
    }
}
