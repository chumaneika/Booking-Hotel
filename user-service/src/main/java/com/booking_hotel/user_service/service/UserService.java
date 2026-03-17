package com.booking_hotel.user_service.service;

import com.booking_hotel.user_service.dto.UserCreateDTO;
import com.booking_hotel.user_service.dto.UserResponseDTO;
import com.booking_hotel.user_service.entity.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserService {
    UserResponseDTO saveUser(UserCreateDTO dto);
    void deleteUser(UUID userId);
    UserResponseDTO updateName(UUID userId, String newName);
    UserResponseDTO updateStatus(UUID userId, Status status);
    UserResponseDTO findUser(UUID id);
    UserResponseDTO findUser(String email);
    boolean checkActivityUser(UUID userId);
    Page<UserResponseDTO> getAllUsers(Status status, Pageable pageable);
}
