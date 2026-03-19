package com.booking_hotel.user_service.service;

import com.booking_hotel.user_service.dto.UserCreateRequestDTO;
import com.booking_hotel.user_service.dto.UserSummaryResponseDTO;
import com.booking_hotel.user_service.dto.UserUpdatePersonalInfoRequestDTO;
import com.booking_hotel.user_service.dto.UserUpdateStatusRequestDTO;
import com.booking_hotel.user_service.entity.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserService {
    UserSummaryResponseDTO saveUser(UserCreateRequestDTO dto);
    void deleteUser(UUID userId);
    UserSummaryResponseDTO updatePersonalInfo(UserUpdatePersonalInfoRequestDTO dto);
    UserSummaryResponseDTO updateStatus(UserUpdateStatusRequestDTO dto);
    UserSummaryResponseDTO findUser(UUID id);
    boolean checkActivityUser(UUID userId);
    Page<UserSummaryResponseDTO> getUsers(Status status, Pageable pageable);
}
