package com.anubhav.airbnb.services;

import com.anubhav.airbnb.models.User;

public interface SessionService
{
    void generateNewSession(User user, String refreshToken);
    void validateSession(String refreshToken);
    void deleteSession(String refreshToken);
    void deleteAllSessions(User user);
}
