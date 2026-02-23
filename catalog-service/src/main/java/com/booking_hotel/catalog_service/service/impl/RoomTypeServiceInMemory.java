package com.booking_hotel.catalog_service.service.impl;

import com.booking_hotel.catalog_service.dto.roomtypedto.RoomTypeCreateDTO;
import com.booking_hotel.catalog_service.dto.roomtypedto.RoomTypeResponseDetailsDTO;
import com.booking_hotel.catalog_service.dto.roomtypedto.RoomTypeResponseSummaryDTO;
import com.booking_hotel.catalog_service.dto.roomtypedto.RoomTypeUpdateDTO;
import com.booking_hotel.catalog_service.service.RoomTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomTypeServiceInMemory implements RoomTypeService {

    @Override
    public List<RoomTypeResponseSummaryDTO> getAllRoomTypes(Long hotelId) {
        return List.of();
    }

    @Override
    public RoomTypeResponseDetailsDTO getRoomTypeById(Long hotelId, Long roomTypeId) {
        return null;
    }

    @Override
    public void addRoomType(Long hotelId, RoomTypeCreateDTO dto) {
        // In-memory implementation - no-op
    }

    @Override
    public void updateRoomType(Long hotelId, Long roomTypeId, RoomTypeUpdateDTO dto) {
        // In-memory implementation - no-op
    }

    @Override
    public void deleteRoomType(Long hotelId, Long roomTypeId) {
        // In-memory implementation - no-op
    }
}
