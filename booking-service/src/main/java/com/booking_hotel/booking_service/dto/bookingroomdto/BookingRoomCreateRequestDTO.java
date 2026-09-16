package com.booking_hotel.booking_service.dto.bookingroomdto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record BookingRoomCreateRequestDTO(
        @NotNull @Min(1) Long roomTypeId,
        @NotNull @Min(1) Integer quantity,
        // Legacy client fields are accepted but never used for server-side pricing.
        BigDecimal pricePerNight,
        Integer nights
) {
}
