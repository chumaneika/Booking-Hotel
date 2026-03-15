package com.booking_hotel.review_service.service.impl;

import com.booking_hotel.review_service.dto.ReviewChangeDataRequestDTO;
import com.booking_hotel.review_service.dto.ReviewCreateRequestDTO;
import com.booking_hotel.review_service.dto.ReviewSummaryResponseDTO;
import com.booking_hotel.review_service.dto.mapper.ReviewMapper;
import com.booking_hotel.review_service.entity.ReviewEntity;
import com.booking_hotel.review_service.repository.ReviewRepository;
import com.booking_hotel.review_service.service.ReviewService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Primary
public class ReviewServiceJpa implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;

    @Override
    public ReviewSummaryResponseDTO createReview(ReviewCreateRequestDTO dto) {
        ReviewEntity reviewEntity = reviewMapper.toEntity(dto);
        ReviewEntity savedReview = reviewRepository.save(reviewEntity);
        return reviewMapper.toSummaryDto(savedReview);
    }

    @Override
    public void deleteReview(Long reviewId) {
        if (!reviewRepository.existsById(reviewId)) {
            throw new EntityNotFoundException("Review with id " + reviewId + " not found");
        }
        reviewRepository.deleteById(reviewId);
    }

    @Override
    public ReviewSummaryResponseDTO updateReviewData(ReviewChangeDataRequestDTO dto) {
        ReviewEntity reviewEntity = reviewRepository.findById(dto.id())
                .orElseThrow(() -> new EntityNotFoundException("Review with id " + dto.id() + " not found"));

        if (dto.rating() != null) {
            reviewEntity.changeRating(dto.rating());
        }
        if (dto.comment() != null) {
            reviewEntity.changeComment(dto.comment());
        }

        ReviewEntity savedReview = reviewRepository.save(reviewEntity);
        return reviewMapper.toSummaryDto(savedReview);
    }
}
