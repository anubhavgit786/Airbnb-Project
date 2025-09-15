package com.anubhav.airbnb.controllers;


import com.anubhav.airbnb.dtos.*;
import com.anubhav.airbnb.services.LoginService;
import com.anubhav.airbnb.services.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController
{
    private final UserService userService;
    private final LoginService loginService;

    @PostMapping("/signup")
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody SignUpRequestDto request)
    {
        UserDto user = userService.createUser(request);

        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto request, HttpServletResponse response)
    {
        LoginResponseDto responseDTO =  loginService.login(request, response);
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtResponseDto> refresh(@CookieValue(value = "refreshToken") String refreshToken)
    {
        JwtResponseDto response = userService.refresh(refreshToken);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
