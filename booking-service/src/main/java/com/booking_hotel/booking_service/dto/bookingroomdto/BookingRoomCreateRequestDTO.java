package com.booking_hotel.booking_service.dto.bookingroomdto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record BookingRoomCreateRequestDTO(
        @NotNull Long roomTypeId,
        @NotNull @Min(1) Integer quantity,
        @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal pricePerNight,
        @NotNull @Min(1) Integer nights
) {
}
