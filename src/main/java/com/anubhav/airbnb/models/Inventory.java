package com.anubhav.airbnb.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(uniqueConstraints = @UniqueConstraint(
        name = "unique_hotel_room_date",
        columnNames = {"hotel_id", "room_id", "date"}),
        indexes = {
                @Index(name = "idx_date", columnList = "date"),
                @Index(name = "idx_city_date", columnList = "city, date")
})
public class Inventory
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private Integer bookedCount;

    @Column(nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private Integer reservedCount;

    @Column(nullable = false)
    private Integer totalCount;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal surgeFactor;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private Boolean closed;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public static Inventory createInventory(Room room, LocalDate date)
    {
        Inventory inventory = new Inventory();
        inventory.setHotel(room.getHotel());
        inventory.setRoom(room);
        inventory.setBookedCount(0);
        inventory.setReservedCount(0);
        inventory.setCity(room.getHotel().getCity());
        inventory.setDate(date);
        inventory.setPrice(room.getBasePrice());
        inventory.setSurgeFactor(BigDecimal.ONE);
        inventory.setTotalCount(room.getTotalCount());
        inventory.setClosed(false);
        return inventory;
    }
}
