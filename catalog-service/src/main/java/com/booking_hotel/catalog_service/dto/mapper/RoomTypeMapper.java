package com.booking_hotel.catalog_service.dto.mapper;


import com.booking_hotel.catalog_service.dto.roomtypedto.RoomTypeResponseDetailsDTO;
import com.booking_hotel.catalog_service.dto.roomtypedto.RoomTypeResponseSummaryDTO;
import com.booking_hotel.catalog_service.entity.RoomTypeEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoomTypeMapper {

    RoomTypeResponseSummaryDTO toSummaryDTO(RoomTypeEntity entity);

    RoomTypeResponseDetailsDTO toDetailsDTO(RoomTypeEntity entity);

}
