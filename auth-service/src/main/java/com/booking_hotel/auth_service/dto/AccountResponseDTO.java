package com.booking_hotel.auth_service.dto;

import com.booking_hotel.auth_service.entity.Role;

public record AccountResponseDTO(
        Long id,
        String email,
        Role role
) {}
