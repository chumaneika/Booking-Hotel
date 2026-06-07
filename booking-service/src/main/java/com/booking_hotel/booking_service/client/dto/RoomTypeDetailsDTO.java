package com.booking_hotel.booking_service.client.dto;

import java.math.BigDecimal;

public record RoomTypeDetailsDTO(
        Long id,
        String name,
        Integer capacity,
        BigDecimal basePrice,
        Integer sizeSqm,
        String bedType,
        Integer quantityRoom
) {
}
