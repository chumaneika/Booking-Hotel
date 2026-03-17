package com.booking_hotel.user_service.service;

import com.booking_hotel.user_service.dto.UserCreateDTO;
import com.booking_hotel.user_service.dto.UserResponseDTO;
import com.booking_hotel.user_service.dto.mapper.UserMapper;
import com.booking_hotel.user_service.entity.Status;
import com.booking_hotel.user_service.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Primary
@RequiredArgsConstructor
@Transactional
public class UserServiceJpa implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponseDTO saveUser(UserCreateDTO dto) {
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
        return null;
    }
}
