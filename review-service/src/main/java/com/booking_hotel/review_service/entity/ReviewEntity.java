package com.booking_hotel.review_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
@NoArgsConstructor
@Getter
public class ReviewEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "review_seq")
    @SequenceGenerator(
            name = "review_seq",
            sequenceName = "review_seq",
            allocationSize = 10
    )
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "hotel_id", nullable = false)
    private Long hotelId;

    @Column(name = "booking_id", nullable = false, unique = true)
    private Long bookingId;

    @Column(name = "rating", nullable = false)
    private Integer rating;

    @Column(name = "comment", length = 2000)
    private String comment;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public ReviewEntity(Long userId, Long hotelId, Long bookingId, Integer rating, String comment) {
        this.userId = userId;
        this.hotelId = hotelId;
        this.bookingId = bookingId;
        this.rating = rating;
        this.comment = comment;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public void assignIdForInMemory(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Id must be positive");
        }
        this.id = id;
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    public void changeRating(Integer rating) {
        if (rating == null || rating > 5 || rating < 1) {
            throw new IllegalArgumentException("Rating value is not valid");
        }

        this.rating = rating;
    }

    public void changeComment(String comment) {
        if (comment != null && comment.length() > 2000) {
            throw new IllegalArgumentException("Comment length must be less than or equal to 2000 characters");
        }
        this.comment = comment;
    }

}
