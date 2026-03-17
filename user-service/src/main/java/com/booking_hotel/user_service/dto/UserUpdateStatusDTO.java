package com.booking_hotel.user_service.dto;


import com.booking_hotel.user_service.entity.Status;
import jakarta.validation.constraints.NotNull;


public record UserUpdateStatusDTO(
        @NotNull
        Status status
) {}
