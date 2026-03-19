package com.booking_hotel.user_service.dto.mapper;


import com.booking_hotel.user_service.dto.UserCreateRequestDTO;
import com.booking_hotel.user_service.dto.UserSummaryResponseDTO;
import com.booking_hotel.user_service.entity.PersonalInfo;
import com.booking_hotel.user_service.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", expression = "java(com.booking_hotel.user_service.entity.Status.ACTIVE)")
    @Mapping(target = "personalInfo", expression = "java(toPersonalInfo(dto))")
    UserEntity toEntity(UserCreateRequestDTO dto);

    @Mapping(target = "firstname", source = "personalInfo.firstname")
    @Mapping(target = "surname", source = "personalInfo.surname")
    @Mapping(target = "birthday", source = "personalInfo.birthday")
    UserSummaryResponseDTO toResponse(UserEntity entity);


    default PersonalInfo toPersonalInfo(UserCreateRequestDTO dto) {
        return new PersonalInfo(
                dto.firstname(),
                dto.surname(),
                dto.birthday()
        );
    }

}
