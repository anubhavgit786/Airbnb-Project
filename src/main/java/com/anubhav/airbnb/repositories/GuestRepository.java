package com.anubhav.airbnb.repositories;

import com.anubhav.airbnb.models.Guest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuestRepository extends JpaRepository<Guest, Long>
{

}
