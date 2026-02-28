package com.booking_hotel.user_service.dto.mapper;

import com.booking_hotel.user_service.dto.UserCreateDTO;
import com.booking_hotel.user_service.dto.UserResponseDTO;
import com.booking_hotel.user_service.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true) // задаётся в сервисе через changePassword()
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "status", ignore = true)
    UserEntity toEntity(UserCreateDTO dto);

    default UserResponseDTO toResponse(UserEntity entity) {
        if (entity == null) return null;
        return new UserResponseDTO(
                entity.getId(),
                entity.getName(),
                entity.getEmail(),
                entity.getRole(),
                entity.getStatus()
        );
    }
}
