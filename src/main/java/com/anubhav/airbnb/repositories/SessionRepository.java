package com.anubhav.airbnb.repositories;

import com.anubhav.airbnb.models.Session;
import com.anubhav.airbnb.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long>
{
    List<Session> findByUser(User user);
    Optional<Session> findByRefreshToken(String refreshToken);
}
