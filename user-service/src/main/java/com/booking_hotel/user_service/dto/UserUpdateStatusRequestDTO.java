package com.booking_hotel.user_service.dto;


import com.booking_hotel.user_service.entity.Status;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;


public record UserUpdateStatusRequestDTO(

        @NotNull
        UUID id,

        @NotNull
        Status status
) {}
