package com.booking_hotel.user_service.dto;

import com.booking_hotel.user_service.entity.Role;
import com.booking_hotel.user_service.entity.Status;

public record UserResponseDTO(
        Long id,
        String name,
        String email,
        Role role,
        Status status
) {}
