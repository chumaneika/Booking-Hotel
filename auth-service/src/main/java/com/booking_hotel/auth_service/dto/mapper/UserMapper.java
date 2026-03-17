package com.booking_hotel.auth_service.dto.mapper;

import com.booking_hotel.auth_service.dto.RegisterRequestDTO;
import com.booking_hotel.auth_service.entity.AccountEntity;
import com.booking_hotel.auth_service.entity.Role;
import org.mapstruct.Mapper;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface UserMapper {

    default AccountEntity toEntity(RegisterRequestDTO dto, String passwordHash) {
        if (dto == null) {
            return null;
        }
        return new AccountEntity(
                UUID.randomUUID(),
                dto.email(),
                passwordHash,
                Role.USER,
                true
        );
    }
}
