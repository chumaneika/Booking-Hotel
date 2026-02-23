package com.booking_hotel.user_service.service;

import com.booking_hotel.user_service.dto.UserCreateDTO;
import com.booking_hotel.user_service.dto.UserResponseDTO;
import com.booking_hotel.user_service.entity.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserResponseDTO createUser(UserCreateDTO dto);
    void deleteUser(Long userId);
    UserResponseDTO updateName(Long userId, String newName);
    UserResponseDTO updateStatus(Long userId, Status status);
    UserResponseDTO findUser(Long id);
    UserResponseDTO findUser(String email);
    boolean checkActivityUser(Long userId);
    Page<UserResponseDTO> getAllUsers(Status status, Pageable pageable);
}
