package com.booking_hotel.user_service.dto;

import jakarta.validation.constraints.NotNull;
import com.booking_hotel.user_service.entity.Status;

public record UserUpdateStatusDTO(

        @NotNull
        Long userId,

        @NotNull
        Status status
) {}
