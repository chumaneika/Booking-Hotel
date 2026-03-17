package com.booking_hotel.user_service.dto.mapper;

import com.booking_hotel.user_service.dto.UserCreateDTO;
import com.booking_hotel.user_service.dto.UserResponseDTO;
import com.booking_hotel.user_service.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    default UserEntity toEntity(UserCreateDTO dto) {
        if (dto == null) return null;
        return new UserEntity(dto.name());
    }

    default UserResponseDTO toResponse(UserEntity entity) {
        if (entity == null) return null;
        return new UserResponseDTO(
                entity.getId(),
                entity.getName(),
                null,
                null,
                entity.getStatus()
        );
    }

}
