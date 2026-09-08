package com.booking_hotel.booking_service.kafka.publisher;

import com.booking_hotel.booking_service.entity.BookingEntity;
import com.booking_hotel.booking_service.entity.BookingRoomEntity;
import com.booking_hotel.booking_service.kafka.events.BookingCancelledEvent;
import com.booking_hotel.booking_service.kafka.events.BookingConfirmedEvent;
import com.booking_hotel.booking_service.kafka.events.BookingCreatedEvent;
import com.booking_hotel.booking_service.kafka.events.BookingExpiredEvent;
import com.booking_hotel.booking_service.kafka.events.PaymentRequestedEvent;
import com.booking_hotel.booking_service.kafka.events.ReservedRoom;
import com.booking_hotel.booking_service.kafka.events.RoomReservationRequestedEvent;
import com.booking_hotel.booking_service.kafka.events.RoomReservationReleasedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BookingEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.kafka.topics.inventory-reservation-commands}")
    private String inventoryReservationCommandsTopic;

    @Value("${app.kafka.topics.payment-commands}")
    private String paymentCommandsTopic;

    @Value("${app.kafka.topics.booking-events}")
    private String bookingEventsTopic;

    @Value("${app.payment.currency}")
    private String paymentCurrency;

    public void publishBookingCreated(BookingEntity booking) {
        publish(bookingEventsTopic, booking.getPublicId(), new BookingCreatedEvent(
                UUID.randomUUID(),
                Instant.now(),
                booking.getPublicId(),
                booking.getUserId(),
                booking.getHotelId(),
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                booking.getTotalPrice(),
                reservedRooms(booking)
        ));
    }

    public void publishRoomReservationRequested(BookingEntity booking) {
        publish(inventoryReservationCommandsTopic, booking.getPublicId(), new RoomReservationRequestedEvent(
                UUID.randomUUID(),
                Instant.now(),
                reservationId(booking),
                booking.getPublicId(),
                booking.getHotelId(),
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                reservedRooms(booking)
        ));
    }

    public void publishPaymentRequested(BookingEntity booking) {
        publish(paymentCommandsTopic, booking.getPublicId(), new PaymentRequestedEvent(
                UUID.randomUUID(),
                Instant.now(),
                paymentId(booking),
                booking.getPublicId(),
                booking.getUserId(),
                booking.getTotalPrice(),
                paymentCurrency
        ));
    }

    public void publishRoomReservationReleased(BookingEntity booking, String reason) {
        publish(inventoryReservationCommandsTopic, booking.getPublicId(), new RoomReservationReleasedEvent(
                UUID.randomUUID(),
                Instant.now(),
                reservationId(booking),
                booking.getPublicId(),
                reason
        ));
    }

    public void publishBookingConfirmed(BookingEntity booking) {
        publish(bookingEventsTopic, booking.getPublicId(), new BookingConfirmedEvent(
                UUID.randomUUID(),
                Instant.now(),
                booking.getPublicId(),
                booking.getUserId(),
                booking.getHotelId(),
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                booking.getTotalPrice()
        ));
    }

    public void publishBookingCancelled(BookingEntity booking, String reason) {
        publish(bookingEventsTopic, booking.getPublicId(), new BookingCancelledEvent(
                UUID.randomUUID(),
                Instant.now(),
                booking.getPublicId(),
                booking.getUserId(),
                booking.getHotelId(),
                reason
        ));
    }

    public void publishBookingExpired(BookingEntity booking) {
        publish(bookingEventsTopic, booking.getPublicId(), new BookingExpiredEvent(
                UUID.randomUUID(),
                Instant.now(),
                booking.getPublicId(),
                booking.getUserId(),
                booking.getHotelId()
        ));
    }

    private List<ReservedRoom> reservedRooms(BookingEntity booking) {
        return booking.getRooms().stream()
                .map(this::toReservedRoom)
                .toList();
    }

    private ReservedRoom toReservedRoom(BookingRoomEntity room) {
        return new ReservedRoom(room.getRoomTypeId(), room.getQuantity());
    }

    private void publish(String topic, UUID bookingPublicId, Object event) {
        try {
            kafkaTemplate.send(topic, bookingPublicId.toString(), objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Cannot serialize Kafka event", exception);
        }
    }

    private UUID reservationId(BookingEntity booking) {
        return UUID.nameUUIDFromBytes(("reservation:" + booking.getPublicId()).getBytes(StandardCharsets.UTF_8));
    }

    private UUID paymentId(BookingEntity booking) {
        return UUID.nameUUIDFromBytes(("payment:" + booking.getPublicId()).getBytes(StandardCharsets.UTF_8));
    }
}
