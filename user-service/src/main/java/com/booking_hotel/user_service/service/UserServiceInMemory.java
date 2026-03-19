package com.booking_hotel.user_service.service;

import com.booking_hotel.user_service.dto.UserCreateRequestDTO;
import com.booking_hotel.user_service.dto.UserSummaryResponseDTO;
import com.booking_hotel.user_service.dto.UserUpdatePersonalInfoRequestDTO;
import com.booking_hotel.user_service.dto.UserUpdateStatusRequestDTO;
import com.booking_hotel.user_service.dto.mapper.UserMapper;
import com.booking_hotel.user_service.entity.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
@RequiredArgsConstructor
public class UserServiceInMemory implements UserService {

    private final UserMapper userMapper;

    @Override
    public UserSummaryResponseDTO saveUser(UserCreateRequestDTO dto) {
        return null;
    }

    @Override
    public void deleteUser(UUID userId) {

    }

    @Override
    public UserSummaryResponseDTO updatePersonalInfo(UserUpdatePersonalInfoRequestDTO dto) {
        return null;
    }

    @Override
    public UserSummaryResponseDTO updateStatus(UserUpdateStatusRequestDTO dto) {
        return null;
    }

    @Override
    public UserSummaryResponseDTO findUser(UUID userId) {
        return null;
    }

    @Override
    public boolean checkActivityUser(UUID userId) {
        return false;
    }

    @Override
    public Page<UserSummaryResponseDTO> getUsers(Status status, Pageable pageable) {
        return Page.empty();
    }
}
