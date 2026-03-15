package com.booking_hotel.review_service.controller;

import com.booking_hotel.review_service.dto.ReviewChangeDataRequestDTO;
import com.booking_hotel.review_service.dto.ReviewCreateRequestDTO;
import com.booking_hotel.review_service.dto.ReviewSummaryResponseDTO;
import com.booking_hotel.review_service.service.ReviewService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/reviews")
@RequiredArgsConstructor
@Validated
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewSummaryResponseDTO> createReview(
            @RequestBody @Valid ReviewCreateRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.createReview(dto));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable @Positive(message = "reviewId must be positive") Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping
    public ResponseEntity<ReviewSummaryResponseDTO> updateReview(
            @RequestBody @Valid ReviewChangeDataRequestDTO dto) {
        return ResponseEntity.ok(reviewService.updateReviewData(dto));
    }
}
