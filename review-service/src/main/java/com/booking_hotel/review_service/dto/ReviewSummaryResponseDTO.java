package com.booking_hotel.review_service.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReviewSummaryResponseDTO(
        Long id,
        Long userId,
        Long hotelId,
        UUID bookingPublicId,
        Integer rating,
        String comment,
        LocalDateTime createdAt
) {}
