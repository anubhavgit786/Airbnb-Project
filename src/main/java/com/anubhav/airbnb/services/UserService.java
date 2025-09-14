package com.anubhav.airbnb.services;

import com.anubhav.airbnb.dtos.*;
import com.anubhav.airbnb.models.User;
import jakarta.servlet.http.HttpServletResponse;

public interface UserService
{
    User getUserByEmail(String email);
    UserDto createUser(SignUpRequestDto request);
    UserDto updateUser(UserUpdateDto request);
    void changePassword(ChangePasswordRequestDto request);
    LoginResponseDto login(LoginRequestDto request, HttpServletResponse response);
    UserDto me();
    JwtResponseDto refresh(String refreshToken);
    User getCurrentUser();
}
