package com.booking_hotel.booking_service.kafka.listener;

import com.booking_hotel.booking_service.kafka.events.PaymentFailedEvent;
import com.booking_hotel.booking_service.kafka.events.PaymentSucceededEvent;
import com.booking_hotel.booking_service.service.BookingService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentEventsListener {

    private final ObjectMapper objectMapper;
    private final BookingService bookingService;

    @KafkaListener(topics = "${app.kafka.topics.payment-events}")
    public void onPaymentEvent(String payload) {
        JsonNode event = readTree(payload);
        if (event.hasNonNull("reason")) {
            PaymentFailedEvent failed = read(payload, PaymentFailedEvent.class);
            bookingService.handlePaymentFailed(failed.bookingPublicId(), failed.reason());
            return;
        }

        PaymentSucceededEvent succeeded = read(payload, PaymentSucceededEvent.class);
        bookingService.handlePaymentSucceeded(succeeded.bookingPublicId());
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
