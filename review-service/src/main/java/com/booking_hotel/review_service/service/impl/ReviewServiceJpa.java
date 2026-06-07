package com.booking_hotel.review_service.service.impl;

import com.booking_hotel.review_service.client.BookingClient;
import com.booking_hotel.review_service.client.dto.BookingDetailsDTO;
import com.booking_hotel.review_service.dto.ReviewChangeDataRequestDTO;
import com.booking_hotel.review_service.dto.ReviewCreateRequestDTO;
import com.booking_hotel.review_service.dto.ReviewSummaryResponseDTO;
import com.booking_hotel.review_service.dto.mapper.ReviewMapper;
import com.booking_hotel.review_service.entity.ReviewEntity;
import com.booking_hotel.review_service.repository.ReviewRepository;
import com.booking_hotel.review_service.security.ReviewAccessGuard;
import com.booking_hotel.review_service.service.ReviewService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Primary
public class ReviewServiceJpa implements ReviewService {
    private static final String REVIEWABLE_BOOKING_STATUS = "CONFIRMED";

    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final BookingClient bookingClient;
    private final ReviewAccessGuard reviewAccessGuard;

    @Override
    public ReviewSummaryResponseDTO createReview(ReviewCreateRequestDTO dto) {
        reviewAccessGuard.ensureCurrentUserOrPrivileged(dto.userId());

        BookingDetailsDTO booking = bookingClient.getBooking(dto.bookingPublicId());
        validateBookingCanBeReviewed(dto, booking);

        if (reviewRepository.existsByBookingPublicId(dto.bookingPublicId())) {
            throw new IllegalStateException("Review already exists for booking: " + dto.bookingPublicId());
        }

        ReviewEntity reviewEntity = reviewMapper.toEntity(dto);
        ReviewEntity savedReview = reviewRepository.save(reviewEntity);
        return reviewMapper.toSummaryDto(savedReview);
    }

    @Override
    public void deleteReview(Long reviewId) {
        ReviewEntity review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("Review with id " + reviewId + " not found"));

        reviewAccessGuard.ensureCanChangeReview(review);
        reviewRepository.delete(review);
    }

    @Override
    public ReviewSummaryResponseDTO updateReviewData(ReviewChangeDataRequestDTO dto) {
        ReviewEntity reviewEntity = reviewRepository.findById(dto.id())
                .orElseThrow(() -> new EntityNotFoundException("Review with id " + dto.id() + " not found"));

        reviewAccessGuard.ensureCanChangeReview(reviewEntity);

        if (dto.rating() != null) {
            reviewEntity.changeRating(dto.rating());
        }
        if (dto.comment() != null) {
            reviewEntity.changeComment(dto.comment());
        }

        ReviewEntity savedReview = reviewRepository.save(reviewEntity);
        return reviewMapper.toSummaryDto(savedReview);
    }

    private void validateBookingCanBeReviewed(ReviewCreateRequestDTO dto, BookingDetailsDTO booking) {
        if (!booking.userId().equals(dto.userId())) {
            throw new IllegalArgumentException("Booking does not belong to the specified user");
        }
        if (!booking.hotelId().equals(dto.hotelId())) {
            throw new IllegalArgumentException("Booking does not belong to the specified hotel");
        }
        if (!REVIEWABLE_BOOKING_STATUS.equals(booking.status())) {
            throw new IllegalStateException("Booking must be " + REVIEWABLE_BOOKING_STATUS + " before review can be created");
        }
    }
}
