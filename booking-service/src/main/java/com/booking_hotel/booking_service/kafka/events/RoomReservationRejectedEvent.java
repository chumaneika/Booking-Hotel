package com.booking_hotel.booking_service.kafka.events;

import java.time.Instant;
import java.util.UUID;

public record RoomReservationRejectedEvent(
        UUID eventId,
        Instant occurredAt,
        UUID reservationId,
        UUID bookingPublicId,
        String reason
) {
}
