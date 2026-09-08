package com.booking_hotel.booking_service.kafka.events;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record BookingConfirmedEvent(
        UUID eventId,
        Instant occurredAt,
        UUID bookingPublicId,
        Long userId,
        Long hotelId,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        BigDecimal totalPrice
) {
}
