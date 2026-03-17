package com.booking_hotel.user_service.dto;

import com.booking_hotel.user_service.entity.Role;
import com.booking_hotel.user_service.entity.Status;

import java.util.UUID;

public record UserResponseDTO(
        UUID id,
        String name,
        String email,
        Role role,
        Status status
) {}
