package com.booking_hotel.catalog_service.dto.hoteldto;

import com.booking_hotel.catalog_service.dto.roomtypedto.RoomTypeResponseSummaryDTO;

import java.util.List;

public record HotelResponseRoomTypeDTO(
    Long id,
    String name,
    List<RoomTypeResponseSummaryDTO> roomTypes
) {}
