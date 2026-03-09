package com.booking_hotel.notification_service.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationStatus {
    SEND("Сообщение отправлено"),
    PENDING("Уведомление создано"),
    FAILED("Ошибка отправка сообщения");

    private final String displayName;
}
