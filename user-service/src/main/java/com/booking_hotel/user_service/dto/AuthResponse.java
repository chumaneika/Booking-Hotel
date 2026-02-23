package com.booking_hotel.user_service.dto;

public record AuthResponse(
        String token,
        String type,
        Long userId,
        String email,
        String name
) {
    public AuthResponse(String token, Long userId, String email, String name) {
        this(token, "Bearer", userId, email, name);
    }
}
