package com.booking_hotel.catalog_service.dto.hoteldto;

public record HotelResponseSummaryDTO(
    Long id,
    String name,
    Integer rating,
    String address // convert to summary box contain country and city
) {}
