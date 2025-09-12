package com.anubhav.airbnb.repositories;

import com.anubhav.airbnb.dtos.HotelPriceDto;
import com.anubhav.airbnb.models.Hotel;
import com.anubhav.airbnb.models.HotelMinPrice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface HotelMinPriceRepository extends JpaRepository<HotelMinPrice, Long>
{
    @Query("""
            SELECT new com.anubhav.airbnb.dtos.HotelPriceDto(h.hotel, AVG(h.price))
            FROM HotelMinPrice h
            WHERE h.hotel.city = :city
                AND h.date BETWEEN :startDate AND :endDate
                AND h.hotel.active = true
           GROUP BY h.hotel
           """)
    Page<HotelPriceDto> findHotelsWithAvailableInventory(
            @Param("city") String city,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("roomsCount") Integer roomsCount,
            @Param("dateCount") Long dateCount,
            Pageable pageable
    );

    Optional<HotelMinPrice> findByHotelAndDate(Hotel hotel, LocalDate date);
}
