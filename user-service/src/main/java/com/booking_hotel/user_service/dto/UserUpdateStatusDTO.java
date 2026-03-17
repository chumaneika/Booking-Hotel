package com.booking_hotel.user_service.dto;

import jakarta.validation.constraints.NotNull;
import com.booking_hotel.user_service.entity.Status;

import java.util.UUID;

public record UserUpdateStatusDTO(

        @NotNull
        UUID userId,

        @NotNull
        Status status
) {}
