package com.booking_hotel.booking_service.kafka.events;

import java.time.Instant;
import java.util.UUID;

public record PaymentFailedEvent(
        UUID eventId,
        Instant occurredAt,
        UUID paymentId,
        UUID bookingPublicId,
        String reason
) {
}
