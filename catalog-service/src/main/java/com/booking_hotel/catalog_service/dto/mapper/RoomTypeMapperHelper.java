package com.booking_hotel.catalog_service.dto.mapper;


import com.booking_hotel.catalog_service.entity.Address;
import com.booking_hotel.catalog_service.repository.RoomTypeRepository;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoomTypeMapperHelper {

    private final RoomTypeRepository roomTypeRepository;

    @Named("convertToSummaryAddress")
    public String convertToSummaryAddress(Address address) {
        return address.cityAndCountry();
    }

}
