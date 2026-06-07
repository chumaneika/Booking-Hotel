package com.booking_hotel.review_service.client.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record BookingDetailsDTO(
        UUID publicId,
        Long userId,
        Long hotelId,
        String status,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        BigDecimal totalPrice,
        Instant createdAt,
        Instant updatedAt
) {
}
