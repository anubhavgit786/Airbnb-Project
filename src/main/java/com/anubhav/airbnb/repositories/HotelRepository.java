package com.anubhav.airbnb.repositories;

import com.anubhav.airbnb.models.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long>
{

}
