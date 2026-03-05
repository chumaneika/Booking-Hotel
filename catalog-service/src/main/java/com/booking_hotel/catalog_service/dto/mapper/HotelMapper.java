package com.booking_hotel.catalog_service.dto.mapper;


import com.booking_hotel.catalog_service.dto.hoteldto.HotelResponseDetailsDTO;
import com.booking_hotel.catalog_service.dto.hoteldto.HotelResponseRoomTypeDTO;
import com.booking_hotel.catalog_service.dto.hoteldto.HotelResponseSummaryDTO;
import com.booking_hotel.catalog_service.entity.HotelEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = RoomTypeMapperHelper.class)
public interface HotelMapper {

    HotelResponseRoomTypeDTO toRoomTypeDTO(HotelEntity entity);

    @Mapping(source = "address", target = "shortAddress", qualifiedByName = "convertToSummaryAddress")
    HotelResponseSummaryDTO toSummaryDTO(HotelEntity entity);

    HotelResponseDetailsDTO toDetailsDTO(HotelEntity entity);
}
