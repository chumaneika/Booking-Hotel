package com.booking_hotel.booking_service.dto.bookingroomdto;

import java.math.BigDecimal;

public record BookingRoomResponseDTO(
        Long id,
        Long roomTypeId,
        Integer quantity,
        BigDecimal pricePerNight,
        Integer nights,
        BigDecimal totalPrice
) {
}
