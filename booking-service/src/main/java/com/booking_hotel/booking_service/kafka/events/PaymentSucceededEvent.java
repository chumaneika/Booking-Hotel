package com.booking_hotel.booking_service.kafka.events;

import java.time.Instant;
import java.util.UUID;

public record PaymentSucceededEvent(
        UUID eventId,
        Instant occurredAt,
        UUID paymentId,
        UUID bookingPublicId,
        String providerPaymentId
) {
}
