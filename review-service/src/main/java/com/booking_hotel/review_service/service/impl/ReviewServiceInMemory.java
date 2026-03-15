package com.booking_hotel.review_service.service.impl;

import com.booking_hotel.review_service.dto.ReviewChangeDataRequestDTO;
import com.booking_hotel.review_service.dto.ReviewCreateRequestDTO;
import com.booking_hotel.review_service.dto.ReviewSummaryResponseDTO;
import com.booking_hotel.review_service.dto.mapper.ReviewMapper;
import com.booking_hotel.review_service.entity.ReviewEntity;
import com.booking_hotel.review_service.service.ReviewService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;


@Service
@RequiredArgsConstructor
public class ReviewServiceInMemory implements ReviewService {
    private final ReviewMapper reviewMapper;
    private final AtomicLong idGenerator = new AtomicLong(1);
    private final Map<Long, ReviewEntity> storage = new ConcurrentHashMap<>();

    @Override
    public ReviewSummaryResponseDTO createReview(ReviewCreateRequestDTO dto) {
        Long id = idGenerator.getAndIncrement();
        ReviewEntity reviewEntity = reviewMapper.toEntity(dto);
        reviewEntity.assignIdForInMemory(id);
        storage.put(id, reviewEntity);
        return reviewMapper.toSummaryDto(reviewEntity);
    }

    @Override
    public void deleteReview(Long reviewId) {
        if (storage.remove(reviewId) == null) {
            throw new EntityNotFoundException("Review with id " + reviewId + " not found");
        }
    }

    @Override
    public ReviewSummaryResponseDTO updateReviewData(ReviewChangeDataRequestDTO dto) {
        ReviewEntity existing = storage.get(dto.id());
        if (existing == null) {
            throw new EntityNotFoundException("Review with id " + dto.id() + " not found");
        }

        if (dto.rating() != null) {
            existing.changeRating(dto.rating());
        }
        if (dto.comment() != null) {
            existing.changeComment(dto.comment());
        }

        storage.put(existing.getId(), existing);
        return reviewMapper.toSummaryDto(existing);
    }
}
