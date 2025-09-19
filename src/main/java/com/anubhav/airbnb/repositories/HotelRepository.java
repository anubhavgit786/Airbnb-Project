package com.anubhav.airbnb.repositories;

import com.anubhav.airbnb.models.Hotel;
import com.anubhav.airbnb.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long>
{
    @Query("SELECT h FROM Hotel h LEFT JOIN FETCH h.rooms WHERE h.id = :hotelId")
    Hotel findByIdWithRooms(@Param("hotelId") Long hotelId);

    List<Hotel> findByOwner(User owner);
}
