package com.booking_hotel.booking_service.dto.bookingdto;

import com.booking_hotel.booking_service.dto.bookingroomdto.BookingRoomResponseDTO;
import com.booking_hotel.booking_service.entity.BookingStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record BookingResponseDTO(
        UUID publicId,
        Long userId,
        Long hotelId,
        BookingStatus status,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        BigDecimal totalPrice,
        Instant createdAt,
        Instant updatedAt,
        List<BookingRoomResponseDTO> rooms
) {
}
