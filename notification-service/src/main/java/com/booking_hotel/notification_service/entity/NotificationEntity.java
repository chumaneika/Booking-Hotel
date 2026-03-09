package com.booking_hotel.notification_service.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notification_seq")
    @SequenceGenerator(
            name = "notification_seq",
            sequenceName = "notification_seq",
            allocationSize = 1
    )
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "recipient", length = 255, nullable = false)
    private String recipient;

    @Column(name = "subject", length = 127, nullable = false)
    private String subject;

    @Column(name = "message", length = 1023)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private NotificationStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public NotificationEntity(Long userId, String recipient, String subject, String message, NotificationType type, NotificationStatus status, LocalDateTime createdAt) {
        this.userId = userId;
        this.recipient = recipient;
        this.subject = subject;
        this.message = message;
        this.type = type;
        this.status = status;
        this.createdAt = createdAt;
    }

    public static NotificationEntity createPending(
            Long userId,
            String recipient,
            String subject,
            String message,
            NotificationType type
    ) {
        NotificationEntity notification = new NotificationEntity();
        notification.userId = userId;
        notification.recipient = recipient;
        notification.subject = subject;
        notification.message = message;
        notification.type = type;
        notification.status = NotificationStatus.PENDING;
        notification.createdAt = LocalDateTime.now();

        return notification;
    }

    public void markSent() {
        this.status = NotificationStatus.SEND;
    }

    public void markFailed() {
        this.status = NotificationStatus.FAILED;
    }

}
