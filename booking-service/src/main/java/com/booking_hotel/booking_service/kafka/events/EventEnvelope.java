package com.booking_hotel.booking_service.kafka.events;

public record EventEnvelope<T>(EventType eventType, T payload) {
    public EventEnvelope {
        if (eventType == null || payload == null) {
            throw new IllegalArgumentException("eventType and payload are required");
        }
    }
}
