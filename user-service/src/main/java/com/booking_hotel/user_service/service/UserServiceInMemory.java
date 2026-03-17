package com.booking_hotel.user_service.service;

import com.booking_hotel.user_service.dto.UserCreateDTO;
import com.booking_hotel.user_service.dto.UserResponseDTO;
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
    public UserResponseDTO createUser(UserCreateDTO dto) {
        return null;
    }

    @Override
    public void deleteUser(UUID userId) {

    }

    @Override
    public UserResponseDTO updateName(UUID userId, String newName) {
        return null;
    }

    @Override
    public UserResponseDTO updateStatus(UUID userId, Status status) {
        return null;
    }

    @Override
    public UserResponseDTO findUser(UUID userId) {
        return null;
    }

    @Override
    public UserResponseDTO findUser(String email) {
        return null;
    }

    @Override
    public boolean checkActivityUser(UUID userId) {
        return false;
    }

    @Override
    public Page<UserResponseDTO> getAllUsers(Status status, Pageable pageable) {
        return Page.empty();
    }
}
