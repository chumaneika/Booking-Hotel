package com.booking_hotel.catalog_service.dto.hoteldto;

import com.booking_hotel.catalog_service.entity.Address;
import com.booking_hotel.catalog_service.entity.StatusHotel;

public record HotelResponseDetailsDTO(
    Long id,
    String name,
    String description,
    Integer rating,
    Address address, // full address
    StatusHotel status
) {}
