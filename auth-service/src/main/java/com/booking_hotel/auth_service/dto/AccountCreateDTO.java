package com.booking_hotel.auth_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AccountCreateDTO(

        @NotBlank
        @Size(min = 2, max = 50)
        String name,

        @NotBlank
        @Size(min = 8, max = 64)
        String password,

        @NotBlank
        @Email
        String email
) {}
