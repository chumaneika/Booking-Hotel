package com.booking_hotel.catalog_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "hotels")
@Getter
@NoArgsConstructor
public class HotelEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hotel_seq")
    @SequenceGenerator(
            name = "hotel_seq",
            sequenceName = "hotel_seq",
            allocationSize = 1
    )
    private Long id;

    @Column(name = "name", nullable = false, length = 120)
    private String name;

    @Column(name = "description", nullable = false, length = 2000)
    private String description;

    @Column(name = "rating", nullable = false)
    private Integer rating;

    @Embedded
    private Address address;

    @OneToMany(mappedBy = "hotel", fetch = FetchType.LAZY)
    private List<RoomTypeEntity> roomTypes;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private StatusHotel status;

    public HotelEntity(String name, String description, Integer rating, Address address, List<RoomTypeEntity> roomTypes, StatusHotel status) {
        this.name = name;
        this.description = description;
        this.rating = rating;
        this.roomTypes = roomTypes;
        this.address = address;
        this.status = status;
    }

    public void changeName(String name) {
        if (name == null || name.isBlank() || name.length() > 120) {
            throw new IllegalArgumentException("Hotel name is invalid");
        } else {
            this.name = name;
        }
    }

    public void changeDescription(String description) {
        if (description == null || description.isBlank() || description.length() > 120) {
            throw new IllegalArgumentException("Hotel name is invalid");
        } else {
            this.description = description;
        }
    }

    public void changeRating(Integer rating) {
        if (rating == null || rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        this.rating = rating;
    }

    public void changeStatus(StatusHotel status) {
        Set<StatusHotel> setStatus = EnumSet.allOf(StatusHotel.class);

        if (status == null) {
            throw new IllegalArgumentException("Status hotel must not be NULL");
        } else if (!setStatus.contains(status)) {
            throw new IllegalArgumentException("Value status doesn't contain one of set component");
        }

        this.status = status;

    }

}
