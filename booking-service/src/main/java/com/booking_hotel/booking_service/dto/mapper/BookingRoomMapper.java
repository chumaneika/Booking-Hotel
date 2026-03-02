package com.booking_hotel.booking_service.dto.mapper;

import com.booking_hotel.booking_service.dto.bookingroomdto.BookingRoomCreateForBookingRequestDTO;
import com.booking_hotel.booking_service.dto.bookingroomdto.BookingRoomCreateRequestDTO;
import com.booking_hotel.booking_service.dto.bookingroomdto.BookingRoomResponseDTO;
import com.booking_hotel.booking_service.entity.BookingEntity;
import com.booking_hotel.booking_service.entity.BookingRoomEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class BookingRoomMapper {

    public BookingRoomEntity toEntity(
            BookingRoomCreateRequestDTO request,
            BookingEntity booking,
            BigDecimal totalPrice
    ) {
        return new BookingRoomEntity(
                booking,
                request.roomTypeId(),
                request.quantity(),
                request.pricePerNight(),
                request.nights(),
                totalPrice
        );
    }

    public BookingRoomEntity toEntity(
            BookingRoomCreateForBookingRequestDTO request,
            BookingEntity booking,
            BigDecimal totalPrice
    ) {
        return new BookingRoomEntity(
                booking,
                request.roomTypeId(),
                request.quantity(),
                request.pricePerNight(),
                request.nights(),
                totalPrice
        );
    }

    public BookingRoomResponseDTO toResponseDTO(BookingRoomEntity room) {
        return new BookingRoomResponseDTO(
                room.getId(),
                room.getRoomTypeId(),
                room.getQuantity(),
                room.getPricePerNight(),
                room.getNights(),
                room.getTotalPrice()
        );
    }
}
