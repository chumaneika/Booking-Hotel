package com.booking_hotel.booking_service;

import com.booking_hotel.booking_service.kafka.events.EventEnvelope;
import com.booking_hotel.booking_service.kafka.events.EventType;
import com.booking_hotel.booking_service.kafka.listener.InventoryReservationEventsListener;
import com.booking_hotel.booking_service.kafka.listener.PaymentEventsListener;
import com.booking_hotel.booking_service.service.BookingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class KafkaEventRoutingTests {
    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
    private final List<String> calls = new ArrayList<>();
    private final UUID bookingId = UUID.randomUUID();
    private final BookingService service = (BookingService) Proxy.newProxyInstance(
            BookingService.class.getClassLoader(), new Class<?>[]{BookingService.class},
            (proxy, method, args) -> {
                assertEquals(bookingId, args[0]);
                calls.add(method.getName());
                return null;
            });
    private final PaymentEventsListener payments = new PaymentEventsListener(mapper, service);
    private final InventoryReservationEventsListener inventory = new InventoryReservationEventsListener(mapper, service);

    private String message(EventType type) throws Exception {
        return mapper.writeValueAsString(new EventEnvelope<>(type, Map.of("bookingPublicId", bookingId)));
    }

    @Test
    void routesFailuresWithoutReasonAsFailures() throws Exception {
        payments.onPaymentEvent(message(EventType.PAYMENT_FAILED));
        inventory.onInventoryReservationEvent(message(EventType.ROOM_RESERVATION_REJECTED));
        assertEquals(List.of("handlePaymentFailed", "handleRoomReservationRejected"), calls);
    }

    @Test
    void routesSuccessfulResults() throws Exception {
        payments.onPaymentEvent(message(EventType.PAYMENT_SUCCEEDED));
        inventory.onInventoryReservationEvent(message(EventType.ROOM_RESERVED));
        assertEquals(List.of("handlePaymentSucceeded", "handleRoomsReserved"), calls);
    }

    @Test
    void rejectsWrongTopicTypes() throws Exception {
        String reserved = message(EventType.ROOM_RESERVED);
        String paid = message(EventType.PAYMENT_SUCCEEDED);
        assertThrows(IllegalArgumentException.class, () -> payments.onPaymentEvent(reserved));
        assertThrows(IllegalArgumentException.class, () -> inventory.onInventoryReservationEvent(paid));
        assertTrue(calls.isEmpty());
    }

    @Test
    void rejectsMalformedAndLegacyMessagesWithoutChangingBooking() {
        for (String input : List.of("null", "{}", "{", "{\"reason\":\"declined\"}",
                "{\"eventType\":\"UNKNOWN\",\"payload\":{\"bookingPublicId\":\"" + bookingId + "\"}}",
                "{\"eventType\":\"PAYMENT_SUCCEEDED\",\"payload\":{}}",
                "{\"eventType\":\"ROOM_RESERVED\",\"payload\":null}")) {
            assertThrows(IllegalArgumentException.class, () -> payments.onPaymentEvent(input));
            assertThrows(IllegalArgumentException.class, () -> inventory.onInventoryReservationEvent(input));
        }
        assertTrue(calls.isEmpty());
    }
}
