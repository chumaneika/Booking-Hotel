package com.booking_hotel.booking_service;

import com.booking_hotel.booking_service.dto.bookingdto.BookingStatusUpdateRequestDTO;
import com.booking_hotel.booking_service.dto.mapper.*;
import com.booking_hotel.booking_service.entity.*;
import com.booking_hotel.booking_service.kafka.publisher.BookingEventPublisher;
import com.booking_hotel.booking_service.repository.BookingRepository;
import com.booking_hotel.booking_service.security.BookingAccessGuard;
import com.booking_hotel.booking_service.service.impl.BookingServiceJpa;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class BookingSagaTests {
    private final UUID id=UUID.randomUUID();
    private final BookingEntity booking=new BookingEntity(id,1L,1L,BookingStatus.NEW,
            LocalDate.now().plusDays(1),LocalDate.now().plusDays(3),new BigDecimal("100"),null,null,new ArrayList<>());
    private final List<String> events=new ArrayList<>();
    private final BookingRepository repository=(BookingRepository)Proxy.newProxyInstance(
            BookingRepository.class.getClassLoader(),new Class<?>[]{BookingRepository.class},(proxy,method,args)-> switch(method.getName()) {
                case "findByPublicId","findLockedByPublicId" -> Optional.of(booking);
                case "save" -> args[0];
                default -> throw new AssertionError("Unexpected repository call");
            });
    private final BookingAccessGuard guard=new BookingAccessGuard() {
        @Override public void ensurePrivileged() {}
        @Override public void ensureCanAccessBooking(BookingEntity booking) {}
    };
    private final BookingEventPublisher publisher=new BookingEventPublisher(null,null) {
        @Override public void publishPaymentRequested(BookingEntity booking) { events.add("payment"); }
        @Override public void publishBookingCancelled(BookingEntity booking,String reason) { events.add("cancelled"); }
        @Override public void publishRoomReservationReleased(BookingEntity booking,String reason) { events.add("release"); }
        @Override public void publishBookingConfirmed(BookingEntity booking) { events.add("confirmed"); }
    };
    private final BookingRoomMapper rooms=new BookingRoomMapper();
    private final BookingServiceJpa service=new BookingServiceJpa(repository,new BookingServiceMapper(rooms),rooms,guard,publisher,null);
    @Test void successfulFlowAndDuplicateResultsAreIdempotent() {
        service.handleRoomsReserved(id); service.handleRoomsReserved(id);
        assertEquals(BookingStatus.PAYMENT_PENDING,booking.getStatus());
        assertThrows(ResponseStatusException.class,()->service.updateBookingStatus(id,new BookingStatusUpdateRequestDTO(BookingStatus.CONFIRMED)));
        service.handlePaymentSucceeded(id); service.handlePaymentSucceeded(id);
        assertEquals(BookingStatus.PAID,booking.getStatus());
        service.updateBookingStatus(id,new BookingStatusUpdateRequestDTO(BookingStatus.CONFIRMED));
        service.updateBookingStatus(id,new BookingStatusUpdateRequestDTO(BookingStatus.CONFIRMED));
        assertEquals(List.of("payment","confirmed"),events);
    }
    @Test void paymentFailureReleasesOnceAndLateSuccessDoesNotReopenBooking() {
        service.handleRoomsReserved(id);
        service.handlePaymentFailed(id,"declined"); service.handlePaymentFailed(id,"declined");
        service.handlePaymentSucceeded(id);
        assertEquals(BookingStatus.CANCELLED,booking.getStatus());
        assertEquals(List.of("payment","cancelled","release"),events);
    }
    @Test void cancelBeforeReservationRetainsHistoryAndReleasesPotentialLateReservation() {
        service.deleteBooking(id); service.deleteBooking(id); service.handleRoomsReserved(id);
        assertEquals(BookingStatus.CANCELLED,booking.getStatus());
        assertEquals(List.of("cancelled","release"),events);
    }
}
