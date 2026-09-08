package com.booking_hotel.booking_service.kafka.events;

import java.time.Instant;
import java.util.UUID;

public record RoomReservedEvent(
        UUID eventId,
        Instant occurredAt,
        UUID reservationId,
        UUID bookingPublicId
) {
}
