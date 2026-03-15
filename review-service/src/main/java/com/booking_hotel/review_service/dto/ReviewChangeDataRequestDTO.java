package com.booking_hotel.review_service.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ReviewChangeDataRequestDTO(
        @NotNull(message = "id is required")
        @Positive(message = "id must be positive")
        Long id,

        @Size(max = 2000, message = "comment length must be less than or equal to 2000 characters")
        String comment,

        @Min(value = 1, message = "rating must be between 1 and 5")
        @Max(value = 5, message = "rating must be between 1 and 5")
        Integer rating
) {}
