package com.anubhav.airbnb.repositories;

import com.anubhav.airbnb.models.Guest;
import com.anubhav.airbnb.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GuestRepository extends JpaRepository<Guest, Long>
{
    List<Guest> findByUser(User user);
}
