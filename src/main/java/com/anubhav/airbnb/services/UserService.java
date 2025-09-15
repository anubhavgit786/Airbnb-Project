package com.anubhav.airbnb.services;

import com.anubhav.airbnb.dtos.*;
import com.anubhav.airbnb.models.User;
import jakarta.servlet.http.HttpServletResponse;

public interface UserService
{
    User getUserByEmail(String email);
    UserDto createUser(SignUpRequestDto request);
    UserDto updateUser(UserUpdateDto request, HttpServletResponse response);
    void changePassword(ChangePasswordRequestDto request);
    UserDto me();
    JwtResponseDto refresh(String refreshToken);
    User getCurrentUser();
    void logout(String refreshToken, HttpServletResponse response);
}
