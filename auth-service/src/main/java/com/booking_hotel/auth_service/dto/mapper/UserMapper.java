package com.booking_hotel.auth_service.dto.mapper;

import com.booking_hotel.auth_service.dto.AccountResponseDTO;
import com.booking_hotel.auth_service.entity.AccountEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    default AccountResponseDTO toResponse(AccountEntity entity) {
        if (entity == null) return null;
        return new AccountResponseDTO(
                entity.getId(),
                entity.getEmail(),
                entity.getRole()
        );
    }
}
