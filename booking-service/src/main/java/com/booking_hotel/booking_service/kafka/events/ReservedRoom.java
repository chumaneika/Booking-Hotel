package com.booking_hotel.booking_service.kafka.events;

public record ReservedRoom(
        Long roomTypeId,
        Integer quantity
) {
}
