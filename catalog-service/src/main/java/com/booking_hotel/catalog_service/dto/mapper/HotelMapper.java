package com.booking_hotel.catalog_service.dto.mapper;


import com.booking_hotel.catalog_service.dto.hoteldto.HotelResponseDetailsDTO;
import com.booking_hotel.catalog_service.dto.hoteldto.HotelResponseRoomTypeDTO;
import com.booking_hotel.catalog_service.dto.hoteldto.HotelResponseSummaryDTO;
import com.booking_hotel.catalog_service.entity.HotelEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = HotelMapperHelper.class)
public interface HotelMapper {

    HotelResponseRoomTypeDTO toRoomTypeDTO(HotelEntity entity);

    @Mapping(source = "address", target = "address", qualifiedByName = "convertToSummaryAddress")
    HotelResponseSummaryDTO toSummaryDTO(HotelEntity entity);

    HotelResponseDetailsDTO toDetailsDTO(HotelEntity entity);
}
