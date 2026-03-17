package com.booking_hotel.user_service.repository;

import com.booking_hotel.user_service.entity.Status;
import com.booking_hotel.user_service.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    boolean existsByIdAndStatus(UUID userId, Status status);

    Page<UserEntity> findByStatus(Status status, Pageable pageable);
}
