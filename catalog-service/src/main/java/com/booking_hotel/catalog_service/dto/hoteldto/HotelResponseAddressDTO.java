package com.booking_hotel.catalog_service.dto.hoteldto;

public record HotelResponseAddressDTO(
        Long id,
        String name,
        String country,
        String city,
        String address
) {}
