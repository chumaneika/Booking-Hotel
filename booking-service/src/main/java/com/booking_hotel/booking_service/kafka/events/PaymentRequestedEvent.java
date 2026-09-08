package com.booking_hotel.booking_service.kafka.events;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentRequestedEvent(
        UUID eventId,
        Instant occurredAt,
        UUID paymentId,
        UUID bookingPublicId,
        Long userId,
        BigDecimal amount,
        String currency
) {
}
