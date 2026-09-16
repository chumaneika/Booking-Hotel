package com.booking_hotel.catalog_service.service;

import com.booking_hotel.catalog_service.entity.*;
import com.booking_hotel.catalog_service.repository.*;
import com.booking_hotel.catalog_service.messaging.Outbox;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service @RequiredArgsConstructor
public class InventoryService {
    public record Room(Long roomTypeId,Integer quantity) {}
    public record Request(UUID reservationId,UUID bookingPublicId,Long hotelId,LocalDate checkInDate,LocalDate checkOutDate,List<Room> rooms) {}
    public record Release(UUID reservationId,UUID bookingPublicId,String reason) {}
    private final InventoryReservationRepository reservations;
    private final InventoryReservationRoomRepository items;
    private final RoomTypeRepository roomTypes;
    private final JdbcTemplate jdbc;
    private final ObjectMapper mapper;
    private final Outbox outbox;
    @Value("${app.kafka.topics.inventory-reservation-events}") private String eventsTopic;

    @Transactional
    public void reserve(Request request) {
        requireIds(request.reservationId(),request.bookingPublicId());
        lock(request.reservationId());
        var existing=reservations.findById(request.reservationId());
        if(existing.isPresent()) {
            if(!existing.get().getBookingPublicId().equals(request.bookingPublicId())) throw new IllegalArgumentException("Reservation identity mismatch");
            publish(existing.get()); return;
        }
        var reservation=new InventoryReservation();
        reservation.setReservationId(request.reservationId()); reservation.setBookingPublicId(request.bookingPublicId());
        String reason=validate(request);
        var locked=new TreeMap<Long,RoomTypeEntity>();
        if(reason==null) {
            for(var room:request.rooms().stream().sorted(Comparator.comparing(Room::roomTypeId)).toList()) {
                var type=roomTypes.findLockedById(room.roomTypeId()).orElse(null);
                if(type==null || !type.getHotel().getId().equals(request.hotelId()) || type.getHotel().getStatus()!=StatusHotel.ACTIVE) {
                    reason="Room type unavailable"; break;
                }
                locked.put(room.roomTypeId(),type);
            }
        }
        if(reason==null) {
            for(var room:request.rooms()) {
                for(LocalDate day=request.checkInDate();day.isBefore(request.checkOutDate());day=day.plusDays(1)) {
                    if(items.occupied(room.roomTypeId(),day)+room.quantity()>locked.get(room.roomTypeId()).getQuantityRoom()) {
                        reason="Not enough rooms for requested dates"; break;
                    }
                }
                if(reason!=null) break;
            }
        }
        reservation.setStatus(reason==null?"RESERVED":"REJECTED"); reservation.setReason(reason);
        reservations.save(reservation);
        if(reason==null) for(var room:request.rooms()) {
            var item=new InventoryReservationRoom(); item.setReservationId(request.reservationId());
            item.setRoomTypeId(room.roomTypeId()); item.setQuantity(room.quantity());
            item.setCheckInDate(request.checkInDate()); item.setCheckOutDate(request.checkOutDate()); items.save(item);
        }
        publish(reservation);
    }

    @Transactional
    public void release(Release request) {
        requireIds(request.reservationId(),request.bookingPublicId()); lock(request.reservationId());
        var reservation=reservations.findById(request.reservationId()).orElseGet(InventoryReservation::new);
        if(reservation.getBookingPublicId()!=null && !reservation.getBookingPublicId().equals(request.bookingPublicId()))
            throw new IllegalArgumentException("Reservation identity mismatch");
        // Lock the same stock rows as reserve; retain a tombstone for a late/duplicate request.
        items.findByReservationId(request.reservationId()).stream().map(InventoryReservationRoom::getRoomTypeId)
                .distinct().sorted().forEach(roomTypes::findLockedById);
        items.deleteByReservationId(request.reservationId());
        reservation.setReservationId(request.reservationId()); reservation.setBookingPublicId(request.bookingPublicId());
        reservation.setStatus("RELEASED"); reservation.setReason("Reservation released"); reservations.save(reservation);
    }

    private String validate(Request r) {
        if(r.hotelId()==null || r.checkInDate()==null || r.checkOutDate()==null || !r.checkOutDate().isAfter(r.checkInDate())
                || r.checkInDate().isBefore(LocalDate.now()) || ChronoUnit.DAYS.between(r.checkInDate(),r.checkOutDate())>365
                || r.rooms()==null || r.rooms().isEmpty()) return "Invalid reservation request";
        var ids=new HashSet<Long>();
        for(var room:r.rooms()) if(room==null || room.roomTypeId()==null || room.quantity()==null || room.quantity()<1
                || !ids.add(room.roomTypeId())) return "Invalid reservation rooms";
        return null;
    }
    private void requireIds(UUID reservation,UUID booking) {
        if(reservation==null || booking==null) throw new IllegalArgumentException("Missing reservation identity");
    }
    private void lock(UUID id) {
        jdbc.queryForList("select pg_advisory_xact_lock(hashtextextended(?,0))",id.toString());
    }
    private void publish(InventoryReservation r) {
        var payload=new LinkedHashMap<String,Object>();
        payload.put("eventId",UUID.randomUUID()); payload.put("occurredAt",Instant.now());
        payload.put("reservationId",r.getReservationId()); payload.put("bookingPublicId",r.getBookingPublicId());
        boolean success=r.getStatus().equals("RESERVED");
        if(!success) payload.put("reason",r.getReason());
        try {
            outbox.enqueue(eventsTopic,r.getBookingPublicId().toString(),mapper.writeValueAsString(
                    Map.of("eventType",success?"ROOM_RESERVED":"ROOM_RESERVATION_REJECTED","payload",payload)));
        } catch(Exception e) { throw new IllegalStateException("Cannot serialize reservation result",e); }
    }
}
