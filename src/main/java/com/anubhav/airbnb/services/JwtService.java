package com.anubhav.airbnb.services;

import com.anubhav.airbnb.enums.Role;
import com.anubhav.airbnb.models.User;

import java.util.Set;

public interface JwtService
{
    String generateAccessToken(User user);
    String generateRefreshToken(User user);
    boolean validateToken(String token);
    String getEmailFromToken(String token);
    Set<Role> getRolesFromToken(String token);
}