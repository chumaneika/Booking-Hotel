package com.booking_hotel.booking_service.kafka.events;

import java.time.Instant;
import java.util.UUID;

public record BookingCancelledEvent(
        UUID eventId,
        Instant occurredAt,
        UUID bookingPublicId,
        Long userId,
        Long hotelId,
        String reason
) {
}
