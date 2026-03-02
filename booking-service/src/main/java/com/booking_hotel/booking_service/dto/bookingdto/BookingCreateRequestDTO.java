package com.booking_hotel.booking_service.dto.bookingdto;

import com.booking_hotel.booking_service.dto.bookingroomdto.BookingRoomCreateRequestDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record BookingCreateRequestDTO(
        @NotNull @Min(1) Long userId,
        @NotNull @Min(1) Long hotelId,
        @NotNull LocalDate checkInDate,
        @NotNull LocalDate checkOutDate,
        @Valid @NotEmpty List<BookingRoomCreateRequestDTO> rooms
) {
}
