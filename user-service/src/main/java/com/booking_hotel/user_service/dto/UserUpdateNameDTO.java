package com.booking_hotel.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserUpdateNameDTO(

        @NotNull
        Long userId,

        @NotBlank
        @Size(min = 2, max = 50)
        String name
) {}
