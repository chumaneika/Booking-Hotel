package com.booking_hotel.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UserUpdatePersonalInfoDTO(

        @NotBlank
        @Size(min = 2, max = 50)
        String firstname,

        @NotBlank
        @Size(min = 2, max = 50)
        String surname,

        @NotNull
        LocalDate birthday

) {}
