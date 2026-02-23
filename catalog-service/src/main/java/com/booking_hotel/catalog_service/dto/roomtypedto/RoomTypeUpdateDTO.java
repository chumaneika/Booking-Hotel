package com.booking_hotel.catalog_service.dto.roomtypedto;

import com.booking_hotel.catalog_service.entity.BedType;
import com.booking_hotel.catalog_service.entity.NameRoomType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record RoomTypeUpdateDTO(
        NameRoomType name,
        
        @Min(1)
        @Max(10)
        Integer capacity,
        
        @DecimalMin(value = "0.01", message = "Base price must be positive")
        BigDecimal basePrice,
        
        @Min(1)
        @Max(500)
        Integer sizeSqm,
        
        BedType bedType,
        
        @Positive
        Integer quantityRoom
) {}
