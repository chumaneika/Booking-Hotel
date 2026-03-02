package com.booking_hotel.booking_service.dto.bookingdto;

import com.booking_hotel.booking_service.entity.BookingStatus;
import jakarta.validation.constraints.NotNull;

public record BookingStatusUpdateRequestDTO(
        @NotNull BookingStatus status
) {
}
