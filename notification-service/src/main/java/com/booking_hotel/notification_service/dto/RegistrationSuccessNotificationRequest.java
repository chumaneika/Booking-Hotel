package com.booking_hotel.notification_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegistrationSuccessNotificationRequest(
        @NotNull(message = "User id is required")
        Long userId,

        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Email should be valid")
        String email
) {
}
