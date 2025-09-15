package com.anubhav.airbnb.services;

import com.anubhav.airbnb.models.Session;
import com.anubhav.airbnb.models.User;
import com.anubhav.airbnb.repositories.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService
{
    private final SessionRepository sessionRepository;

    private final int SESSION_LIMIT=2;


    @Override
    public void generateNewSession(User user, String refreshToken)
    {
        List<Session> userSession = sessionRepository.findByUser(user);

        if(userSession.size() == SESSION_LIMIT)
        {
            userSession.sort(Comparator.comparing(Session::getLastUsedAt));

            Session leastRecentlyUsedSession = userSession.getFirst();
            sessionRepository.delete(leastRecentlyUsedSession);
        }


        Session newSession = Session.builder()
                .user(user)
                .refreshToken(refreshToken)
                .build();

        sessionRepository.save(newSession);
    }

    @Override
    public void validateSession(String refreshToken)
    {
        Session session =  sessionRepository.findByRefreshToken(refreshToken)
                .orElseThrow(()-> new SessionAuthenticationException("Session not found for refreshToken: " + refreshToken));
        session.setLastUsedAt(LocalDateTime.now());
        sessionRepository.save(session);
    }

    @Override
    public void deleteSession(String refreshToken)
    {
        sessionRepository.findByRefreshToken(refreshToken)
                .ifPresent(sessionRepository::delete);
    }

    @Override
    public void deleteAllSessions(User user)
    {
        List<Session> sessions = sessionRepository.findByUser(user);
        sessionRepository.deleteAll(sessions);
    }
}
