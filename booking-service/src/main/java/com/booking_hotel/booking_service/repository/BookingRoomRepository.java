package com.booking_hotel.booking_service.repository;

import com.booking_hotel.booking_service.entity.BookingRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BookingRoomRepository extends JpaRepository<BookingRoomEntity, Long> {
    List<BookingRoomEntity> findAllByBookingPublicId(UUID bookingPublicId);
}
