package com.booking_hotel.booking_service.kafka.events;

import java.time.Instant;
import java.util.UUID;

public record RoomReservationReleasedEvent(
        UUID eventId,
        Instant occurredAt,
        UUID reservationId,
        UUID bookingPublicId,
        String reason
) {
}
