package com.booking_hotel.catalog_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Entity
@Table(name = "room_types")
@Getter
@NoArgsConstructor
public class RoomTypeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "room_type_seq")
    @SequenceGenerator(
            name = "room_type_seq",
            sequenceName = "room_type_seq",
            allocationSize = 1
    )
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "hotel_id", nullable = false)
    private HotelEntity hotel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NameRoomType name;

    @Column(nullable = false)
    private Integer capacity;

    @Column(name = "base_price", nullable = false)
    private BigDecimal basePrice;

    @Column(name = "size_sqm", nullable = false)
    private Integer sizeSqm;

    @Enumerated(EnumType.STRING)
    @Column(name = "bed_type", nullable = false)
    private BedType bedType;

    @Column(name = "quantity_room")
    private Integer quantityRoom;

    public RoomTypeEntity(HotelEntity hotel, BedType bedType, Integer capacity, NameRoomType name, BigDecimal basePrice, Integer sizeSqm, Integer quantityRoom) {
        this.hotel = hotel;
        this.bedType = bedType;
        this.capacity = capacity;
        this.name = name;
        this.basePrice = basePrice;
        this.sizeSqm = sizeSqm;
        this.quantityRoom = quantityRoom;
    }

    public void changeHotel(HotelEntity hotel) {
        if (hotel == null) {
            throw new IllegalArgumentException("Hotel must not be null");
        }

        this.hotel = hotel;
    }

    public void changeRoomType(NameRoomType name) {
        if (name == null) {
            throw new IllegalArgumentException("Room type name must not be null");
        }
        this.name = name;
    }

    public void changeCapacity(int capacity) {
        if (capacity <= 0 || capacity > 10) { // верхний предел — доменное решение
            throw new IllegalArgumentException("Capacity must be between 1 and 10");
        }
        this.capacity = capacity;
    }

    public void changeBasePrice(BigDecimal basePrice) {
        if (basePrice == null || basePrice.signum() <= 0) {
            throw new IllegalArgumentException("Base price must be positive");
        }
        this.basePrice = basePrice;
    }

    public void changeSizeSqm(int sizeSqm) {
        if (sizeSqm <= 0 || sizeSqm > 500) {
            throw new IllegalArgumentException("Room size must be between 1 and 500 sqm");
        }
        this.sizeSqm = sizeSqm;
    }

    public void changeBedType(BedType bedType) {
        if (bedType == null) {
            throw new IllegalArgumentException("Bed type must not be null");
        }
        this.bedType = bedType;
    }

}

