package com.anubhav.airbnb.services;

import com.anubhav.airbnb.config.JwtConfig;
import com.anubhav.airbnb.dtos.LoginRequestDto;
import com.anubhav.airbnb.dtos.LoginResponseDto;
import com.anubhav.airbnb.exceptions.ResourceNotFoundException;
import com.anubhav.airbnb.repositories.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService
{
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final JwtConfig jwtConfig;
    private final SessionService sessionService;

    @Override
    public LoginResponseDto login(LoginRequestDto request, HttpServletResponse response)
    {
        var authToken = new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
        authenticationManager.authenticate(authToken);
        var user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new ResourceNotFoundException("User not found."));
        var accessToken = jwtService.generateAccessToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        var cookie = new Cookie("refreshToken", refreshToken);

        cookie.setHttpOnly(true);
        cookie.setPath("/auth/refresh");
        cookie.setMaxAge(jwtConfig.getRefreshTokenExpiration().intValue());
        cookie.setSecure(true);

        response.addCookie(cookie);

        sessionService.generateNewSession(user, refreshToken);

        return new LoginResponseDto(accessToken);
    }
}
