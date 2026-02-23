package com.booking_hotel.user_service.repository;

import com.booking_hotel.user_service.entity.Status;
import com.booking_hotel.user_service.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);

    boolean existsByIdAndStatus(Long userId, Status status);

    Page<UserEntity> findByStatus(Status status, Pageable pageable);
}
