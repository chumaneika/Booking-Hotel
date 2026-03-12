package com.booking_hotel.notification_service.service;

import com.booking_hotel.notification_service.entity.NotificationType;

public interface NotificationSender {
    boolean supports(NotificationType type);

    void send(String recipient, String subject, String message);
}
