package com.booking_hotel.notification_service.service.impl;

import com.booking_hotel.notification_service.entity.NotificationType;
import com.booking_hotel.notification_service.service.NotificationSender;
import org.springframework.stereotype.Service;

@Service
public class PushNotificationSender implements NotificationSender {

    @Override
    public boolean supports(NotificationType type) {
        return type == NotificationType.PUSH;
    }

    @Override
    public void send(String recipient, String subject, String message) {
        throw new UnsupportedOperationException("Push notifications are not implemented yet");
    }
}
