package com.booking_hotel.catalog_service.dto.roomtypedto;

import com.booking_hotel.catalog_service.entity.BedType;
import com.booking_hotel.catalog_service.entity.NameRoomType;

import java.math.BigDecimal;

public record RoomTypeResponseDetailsDTO(
        Long id,
        NameRoomType name,
        Integer capacity,
        BigDecimal basePrice,
        Integer sizeSqm,
        BedType bedType,
        Integer quantityRoom
) {}
