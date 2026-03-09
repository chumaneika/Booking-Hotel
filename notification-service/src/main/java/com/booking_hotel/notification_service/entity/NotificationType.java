package com.booking_hotel.notification_service.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType {
    EMAIL("По электронной почте"),
    SMS("По смс-сообщению"),
    PUSH("Push уведомление");

    private final String displayName;
}
