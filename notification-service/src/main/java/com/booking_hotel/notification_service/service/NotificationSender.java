package com.booking_hotel.notification_service.service;


public interface NotificationSender {
    void send(String recipient, String subject, String message);
}
