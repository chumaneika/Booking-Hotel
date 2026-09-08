package com.booking_hotel.booking_service.kafka.events;

import java.time.Instant;
import java.util.UUID;

public record BookingExpiredEvent(
        UUID eventId,
        Instant occurredAt,
        UUID bookingPublicId,
        Long userId,
        Long hotelId
) {
}
