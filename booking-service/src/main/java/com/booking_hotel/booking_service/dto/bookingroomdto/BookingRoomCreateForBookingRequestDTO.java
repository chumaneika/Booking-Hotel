package com.booking_hotel.booking_service.dto.bookingroomdto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record BookingRoomCreateForBookingRequestDTO(
        @NotNull UUID bookingPublicId,
        @NotNull Long roomTypeId,
        @NotNull @Min(1) Integer quantity
) {
}
