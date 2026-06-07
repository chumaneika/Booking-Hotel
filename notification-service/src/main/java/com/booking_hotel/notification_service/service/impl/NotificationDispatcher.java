package com.booking_hotel.notification_service.service.impl;

import com.booking_hotel.notification_service.entity.NotificationEntity;
import com.booking_hotel.notification_service.entity.NotificationType;
import com.booking_hotel.notification_service.repository.NotificationRepository;
import com.booking_hotel.notification_service.service.NotificationSender;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationDispatcher {

    private static final Logger log = LoggerFactory.getLogger(NotificationDispatcher.class);

    private final NotificationRepository notificationRepository;
    private final List<NotificationSender> notificationSenders;

    @Async
    @Transactional
    public void dispatch(Long notificationId) {
        NotificationEntity notification = notificationRepository.findById(notificationId)
                .orElse(null);

        if (notification == null) {
            log.warn("Notification {} was not found for dispatch", notificationId);
            return;
        }

        try {
            resolveSender(notification.getType()).send(
                    notification.getRecipient(),
                    notification.getSubject(),
                    notification.getMessage()
            );
            notification.markSent();
        } catch (RuntimeException exception) {
            notification.markFailed();
            log.warn("Notification {} dispatch failed", notificationId, exception);
        }
    }

    private NotificationSender resolveSender(NotificationType type) {
        return notificationSenders.stream()
                .filter(sender -> sender.supports(type))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No sender found for type: " + type));
    }
}
