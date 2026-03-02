package com.booking_hotel.booking_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "booking_rooms")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookingRoomEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "booking_room_seq")
    @SequenceGenerator(
            name = "booking_room_seq",
            sequenceName = "booking_room_seq",
            allocationSize = 1
    )
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private BookingEntity booking;

    @Column(name = "room_type_id", nullable = false)
    private Long roomTypeId;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "price_per_night", nullable = false, precision = 19, scale = 2)
    private BigDecimal pricePerNight;

    @Column(name = "nights", nullable = false)
    private Integer nights;

    @Column(name = "total_price", nullable = false, precision = 19, scale = 2)
    private BigDecimal totalPrice;

    public BookingRoomEntity(BookingEntity booking, Long roomTypeId, Integer quantity, BigDecimal pricePerNight, Integer nights, BigDecimal totalPrice) {
        this.booking = booking;
        this.roomTypeId = roomTypeId;
        this.quantity = quantity;
        this.pricePerNight = pricePerNight;
        this.nights = nights;
        this.totalPrice = totalPrice;
    }

    public void changeQuantity(int quantity) {
        this.quantity = quantity;
        this.totalPrice = pricePerNight
                .multiply(BigDecimal.valueOf(nights))
                .multiply(BigDecimal.valueOf(quantity));
    }

    public void updateTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void assignToBooking(BookingEntity booking) {
        this.booking = booking;
    }
}
