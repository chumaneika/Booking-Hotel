package com.booking_hotel.user_service.dto;

import com.booking_hotel.user_service.entity.Status;

import java.time.LocalDate;
import java.util.UUID;

public record UserResponseDTO(
        UUID id,
        String firstname,
        String surname,
        LocalDate birthday,
        Status status
) {}
