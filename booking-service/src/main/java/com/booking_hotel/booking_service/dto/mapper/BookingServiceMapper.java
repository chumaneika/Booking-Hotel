package com.booking_hotel.booking_service.dto.mapper;

import com.booking_hotel.booking_service.dto.bookingdto.BookingCreateRequestDTO;
import com.booking_hotel.booking_service.dto.bookingdto.BookingResponseDTO;
import com.booking_hotel.booking_service.entity.BookingEntity;
import com.booking_hotel.booking_service.entity.BookingStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class BookingServiceMapper {

    private final BookingRoomMapper bookingRoomMapper;

    public BookingEntity toEntity(BookingCreateRequestDTO request) {
        return new BookingEntity(
                null,
                request.userId(),
                request.hotelId(),
                BookingStatus.NEW,
                request.checkInDate(),
                request.checkOutDate(),
                BigDecimal.ZERO,
                null,
                null,
                new ArrayList<>()
        );
    }

    public BookingResponseDTO toResponseDTO(BookingEntity booking) {
        return new BookingResponseDTO(
                booking.getPublicId(),
                booking.getUserId(),
                booking.getHotelId(),
                booking.getStatus(),
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                booking.getTotalPrice(),
                booking.getCreatedAt(),
                booking.getUpdatedAt(),
                booking.getRooms().stream().map(bookingRoomMapper::toResponseDTO).toList()
        );
    }
}
