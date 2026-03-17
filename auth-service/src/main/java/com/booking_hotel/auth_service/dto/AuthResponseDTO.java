package com.booking_hotel.auth_service.dto;

public record AuthResponseDTO(
        String token,
        String refreshToken,
        String type,
        Long userId,
        String email,
        String firstname,
        String surname
) {
    public AuthResponseDTO(String token, String refreshToken, Long userId, String email, String firstname, String surname) {
        this(token, refreshToken, "Bearer", userId, email, firstname, surname);
    }
}
