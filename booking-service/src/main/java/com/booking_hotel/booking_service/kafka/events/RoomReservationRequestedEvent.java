package com.booking_hotel.booking_service.kafka.events;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record RoomReservationRequestedEvent(
        UUID eventId,
        Instant occurredAt,
        UUID reservationId,
        UUID bookingPublicId,
        Long hotelId,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        List<ReservedRoom> rooms
) {
}
