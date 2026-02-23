package com.booking_hotel.catalog_service.dto.roomtypedto;

import com.booking_hotel.catalog_service.entity.BedType;
import com.booking_hotel.catalog_service.entity.NameRoomType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record RoomTypeCreateDTO(
        @NotNull
        NameRoomType name,
        
        @NotNull
        Long hotelId,
        
        @NotNull
        @Min(1)
        @Max(10)
        Integer capacity,
        
        @NotNull
        @DecimalMin(value = "0.01", message = "Base price must be positive")
        BigDecimal basePrice,
        
        @NotNull
        @Min(1)
        @Max(500)
        Integer sizeSqm,
        
        @NotNull
        BedType bedType
) {}
