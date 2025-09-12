package com.anubhav.airbnb.repositories;

import com.anubhav.airbnb.models.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long>
{

}
