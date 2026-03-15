package com.booking_hotel.review_service.dto.mapper;

import com.booking_hotel.review_service.dto.ReviewCreateRequestDTO;
import com.booking_hotel.review_service.dto.ReviewSummaryResponseDTO;
import com.booking_hotel.review_service.entity.ReviewEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    ReviewEntity toEntity(ReviewCreateRequestDTO dto);

    ReviewSummaryResponseDTO toSummaryDto(ReviewEntity entity);
}
