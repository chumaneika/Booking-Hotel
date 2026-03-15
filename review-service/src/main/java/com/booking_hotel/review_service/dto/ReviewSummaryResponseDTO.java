package com.booking_hotel.review_service.dto;

import java.time.LocalDateTime;

public record ReviewSummaryResponseDTO(
        Long id,
        Long userId,
        Long hotelId,
        Integer rating,
        String comment,
        LocalDateTime createdAt
) {}
