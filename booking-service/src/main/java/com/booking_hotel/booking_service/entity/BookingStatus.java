package com.booking_hotel.booking_service.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BookingStatus {
    NEW("Новое"),
    ROOM_RESERVED("Номер зарезервирован"),
    PAYMENT_PENDING("Ожидание оплаты"),
    PAID("Оплачено"),
    CONFIRMED("Подтверждено"),
    CANCELLED("Отменено"),
    EXPIRED("Истекло");

    private final String displayName;
}
