package com.booking_hotel.catalog_service.dto.roomtypedto;

import com.booking_hotel.catalog_service.entity.BedType;
import com.booking_hotel.catalog_service.entity.NameRoomType;

public record RoomTypeResponseSummaryDTO(
        NameRoomType name,
        Integer capacity,
        Integer sizeSqm,
        BedType bedType
) {}
