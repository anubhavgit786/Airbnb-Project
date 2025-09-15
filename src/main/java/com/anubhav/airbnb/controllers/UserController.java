package com.anubhav.airbnb.controllers;

import com.anubhav.airbnb.dtos.ChangePasswordRequestDto;
import com.anubhav.airbnb.dtos.UserDto;
import com.anubhav.airbnb.dtos.UserUpdateDto;
import com.anubhav.airbnb.services.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController
{
    private final UserService userService;

    @PutMapping("/update")
    public ResponseEntity<UserDto> updateUser(@Valid @RequestBody UserUpdateDto request, HttpServletResponse response)
    {
        UserDto user = userService.updateUser(request, response);
        return ResponseEntity.ok(user);
    }

    @PatchMapping("/change-password")
    public  ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequestDto request)
    {
        userService.changePassword(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> me()
    {
        UserDto user = userService.me();
        return ResponseEntity.ok(user);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@CookieValue(value = "refreshToken") String refreshToken, HttpServletResponse response)
    {
        userService.logout(refreshToken, response);
        return ResponseEntity.noContent().build();
    }
}
