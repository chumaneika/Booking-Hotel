package com.booking_hotel.catalog_service.dto.hoteldto;

import com.booking_hotel.catalog_service.entity.StatusHotel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record HotelCreateDTO(
    // We don't need to create hotels. We must import them from global api source. Given DTO created for test develop hotel Entities

    @NotBlank
    @Size(min = 3, max = 120)
    String name,

    @NotBlank
    @Size(max = 2000)
    String description,

    @NotBlank
    @Size(max = 100)
    String country,

    @NotBlank
    @Size(max = 100)
    String city,

    @NotBlank
    @Size(max = 255)
    String address,

    @NotNull
    StatusHotel status
) {}