package com.booking_hotel.booking_service.repository;

import com.booking_hotel.booking_service.entity.BookingEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<BookingEntity, Long>, JpaSpecificationExecutor<BookingEntity> {
    @EntityGraph(attributePaths = "rooms")
    Optional<BookingEntity> findByPublicId(UUID publicId);

    @EntityGraph(attributePaths = "rooms")
    List<BookingEntity> findAllByUserId(Long userId);

    @EntityGraph(attributePaths = "rooms")
    List<BookingEntity> findAllByHotelId(Long hotelId);
}
