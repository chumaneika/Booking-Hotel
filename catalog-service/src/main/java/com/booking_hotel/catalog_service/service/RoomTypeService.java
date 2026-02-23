package com.booking_hotel.catalog_service.service;

import com.booking_hotel.catalog_service.dto.roomtypedto.RoomTypeCreateDTO;
import com.booking_hotel.catalog_service.dto.roomtypedto.RoomTypeResponseDetailsDTO;
import com.booking_hotel.catalog_service.dto.roomtypedto.RoomTypeResponseSummaryDTO;
import com.booking_hotel.catalog_service.dto.roomtypedto.RoomTypeUpdateDTO;

import java.util.List;

public interface RoomTypeService {
    List<RoomTypeResponseSummaryDTO> getAllRoomTypes(Long hotelId);
    
    RoomTypeResponseDetailsDTO getRoomTypeById(Long hotelId, Long roomTypeId);
    
    void addRoomType(Long hotelId, RoomTypeCreateDTO dto);
    
    void updateRoomType(Long hotelId, Long roomTypeId, RoomTypeUpdateDTO dto);
    
    void deleteRoomType(Long hotelId, Long roomTypeId);
}
