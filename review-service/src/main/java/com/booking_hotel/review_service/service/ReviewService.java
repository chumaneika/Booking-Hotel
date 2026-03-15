package com.booking_hotel.review_service.service;

import com.booking_hotel.review_service.dto.ReviewChangeDataRequestDTO;
import com.booking_hotel.review_service.dto.ReviewCreateRequestDTO;
import com.booking_hotel.review_service.dto.ReviewSummaryResponseDTO;

public interface ReviewService {
    ReviewSummaryResponseDTO createReview(ReviewCreateRequestDTO dto);
    void deleteReview(Long reviewId);
    ReviewSummaryResponseDTO updateReviewData(ReviewChangeDataRequestDTO dto);
}
