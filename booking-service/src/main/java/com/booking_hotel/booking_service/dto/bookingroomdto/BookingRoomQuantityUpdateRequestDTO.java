package com.booking_hotel.booking_service.dto.bookingroomdto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record BookingRoomQuantityUpdateRequestDTO(
        @NotNull @Min(1) Integer quantity
) {
}
