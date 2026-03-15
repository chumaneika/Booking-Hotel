package com.booking_hotel.review_service.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ReviewCreateRequestDTO(
        @NotNull(message = "userId is required")
        @Positive(message = "userId must be positive")
        Long userId,

        @NotNull(message = "hotelId is required")
        @Positive(message = "hotelId must be positive")
        Long hotelId,

        @NotNull(message = "bookingId is required")
        @Positive(message = "bookingId must be positive")
        Long bookingId,

        @Size(max = 2000, message = "comment length must be less than or equal to 2000 characters")
        String comment,

        @NotNull(message = "rating is required")
        @Min(value = 1, message = "rating must be between 1 and 5")
        @Max(value = 5, message = "rating must be between 1 and 5")
        Integer rating
) {}
