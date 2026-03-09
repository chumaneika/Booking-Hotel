package com.booking_hotel.notification_service.service;

import com.booking_hotel.notification_service.dto.RegistrationSuccessNotificationRequest;

public interface NotificationService {

    void sendRegistrationSuccessEmail(RegistrationSuccessNotificationRequest request);
}
