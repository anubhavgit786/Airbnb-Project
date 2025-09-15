package com.anubhav.airbnb.services;

import com.anubhav.airbnb.dtos.LoginRequestDto;
import com.anubhav.airbnb.dtos.LoginResponseDto;
import jakarta.servlet.http.HttpServletResponse;

public interface LoginService
{
    LoginResponseDto login(LoginRequestDto request, HttpServletResponse response);
}
