package com.booking_hotel.catalog_service.dto.mapper;

import com.booking_hotel.catalog_service.entity.Address;
import org.mapstruct.Named;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HotelMapperHelper {

    @Named("convertToSummaryAddress")
    public String convertToSummaryAddress(Address address) {
        return address.cityAndCountry();
    }
}
