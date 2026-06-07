package com.booking_hotel.review_service.repository;

import com.booking_hotel.review_service.entity.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {
    boolean existsByBookingPublicId(UUID bookingPublicId);
}
