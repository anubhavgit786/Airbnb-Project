package com.anubhav.airbnb.controllers;


import com.anubhav.airbnb.dtos.*;
import com.anubhav.airbnb.services.LoginService;
import com.anubhav.airbnb.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
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
    @Operation(summary = "Create a new account", tags = {"Auth"})
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody SignUpRequestDto request)
    {
        UserDto user = userService.createUser(request);

        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    @Operation(summary = "Login request", tags = {"Auth"})
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto request, HttpServletResponse response)
    {
        LoginResponseDto responseDTO =  loginService.login(request, response);
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh the JWT with a refresh token", tags = {"Auth"})
    public ResponseEntity<JwtResponseDto> refresh(@CookieValue(value = "refreshToken") String refreshToken)
    {
        JwtResponseDto response = userService.refresh(refreshToken);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
