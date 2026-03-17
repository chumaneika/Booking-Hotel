package com.booking_hotel.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record UserUpdateNameDTO(

        @NotNull
        UUID userId,

        @NotBlank
        @Size(min = 2, max = 50)
        String name
) {}
