package com.booking_hotel.catalog_service.dto.hoteldto;

import com.booking_hotel.catalog_service.entity.StatusHotel;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record HotelUpdateDTO(
        @Size(min = 3, max = 120)
        String name,

        @Size(max = 2000)
        String description,

        @Min(1)
        @Max(5)
        Integer rating,

        @Size(max = 100)
        String country,

        @Size(max = 100)
        String city,

        @Size(max = 255)
        String address,

        StatusHotel status
) {}
