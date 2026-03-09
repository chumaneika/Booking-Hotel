package com.booking_hotel.notification_service.repository;

import com.booking_hotel.notification_service.entity.NotificationEntity;
import com.booking_hotel.notification_service.entity.NotificationStatus;
import com.booking_hotel.notification_service.entity.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {
    List<NotificationEntity> findAllByStatusOrderByCreatedAtAsc(NotificationStatus status);

    List<NotificationEntity> findAllByTypeAndStatusOrderByCreatedAtAsc(
            NotificationType type,
            NotificationStatus status
    );

    List<NotificationEntity> findAllByUserIdOrderByCreatedAtDesc(Long userId);

}
