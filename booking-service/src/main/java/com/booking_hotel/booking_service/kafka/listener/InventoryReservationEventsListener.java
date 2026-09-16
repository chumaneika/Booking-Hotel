package com.booking_hotel.booking_service.kafka.listener;

import com.booking_hotel.booking_service.kafka.events.RoomReservationRejectedEvent;
import com.booking_hotel.booking_service.kafka.events.RoomReservedEvent;
import com.booking_hotel.booking_service.service.BookingService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InventoryReservationEventsListener {

    private final ObjectMapper objectMapper;
    private final BookingService bookingService;

    @KafkaListener(topics = "${app.kafka.topics.inventory-reservation-events}")
    public void onInventoryReservationEvent(String payload) {
        JsonNode event = readTree(payload);
        if (event == null || !event.path("eventType").isTextual()
                || !event.path("payload").isObject()) {
            throw new IllegalArgumentException("eventType and object payload are required");
        }
        String eventType = event.get("eventType").textValue();
        JsonNode body = event.get("payload");
        if (!body.path("bookingPublicId").isTextual()) {
            throw new IllegalArgumentException("bookingPublicId is required");
        }
        java.util.UUID.fromString(body.get("bookingPublicId").textValue());
        if ("ROOM_RESERVATION_REJECTED".equals(eventType)) {
            RoomReservationRejectedEvent rejected = read(body.toString(), RoomReservationRejectedEvent.class);
            bookingService.handleRoomReservationRejected(rejected.bookingPublicId(), rejected.reason());
            return;
        }

        if (!"ROOM_RESERVED".equals(eventType)) {
            throw new IllegalArgumentException("Unsupported eventType: " + eventType);
        }
        RoomReservedEvent reserved = read(body.toString(), RoomReservedEvent.class);
        bookingService.handleRoomsReserved(reserved.bookingPublicId());
    }

    private <T> T read(String payload, Class<T> eventType) {
        try {
            return objectMapper.readValue(payload, eventType);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Cannot deserialize Kafka event: " + eventType.getSimpleName(), exception);
        }
    }

    private JsonNode readTree(String payload) {
        try {
            return objectMapper.readTree(payload);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Cannot deserialize Kafka event", exception);
        }
    }
}
