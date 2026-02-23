package com.booking_hotel.user_service.service;

import com.booking_hotel.user_service.dto.UserCreateDTO;
import com.booking_hotel.user_service.dto.UserResponseDTO;
import com.booking_hotel.user_service.dto.mapper.UserMapper;
import com.booking_hotel.user_service.entity.Status;
import com.booking_hotel.user_service.entity.UserEntity;
import com.booking_hotel.user_service.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Primary
@RequiredArgsConstructor
public class UserServiceJpa implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponseDTO createUser(UserCreateDTO dto) {
        UserEntity createdUser = userMapper.toEntity(dto);
        createdUser.changePassword(passwordEncoder.encode(dto.password()));

        userRepository.save(createdUser);
        return userMapper.toResponse(createdUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User is not found"));

        user.changeStatus(Status.DELETED);
        userRepository.save(user);

    }

    @Override
    @Transactional
    public UserResponseDTO updateName(Long userId, String newName) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User is not found"));

        user.changeName(newName);
        userRepository.save(user);

        return userMapper.toResponse(user);
    }

    @Override
    public UserResponseDTO updateStatus(Long userId, Status status) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User is not found"));

        user.changeStatus(status);
        userRepository.save(user);

        return userMapper.toResponse(user);
    }

    @Override
    public UserResponseDTO findUser(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User is not found"));

        return userMapper.toResponse(user);
    }

    @Override
    public UserResponseDTO findUser(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User is not found"));

        return userMapper.toResponse(user);
    }

    @Override
    public boolean checkActivityUser(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User is not found"));

        return !user.getStatus().equals(Status.DELETED);
    }

    @Override
    public Page<UserResponseDTO> getAllUsers(Status status, Pageable pageable) {
        Page<UserEntity> users;
        if (status != null) {
            users = userRepository.findByStatus(status, pageable);
        } else {
            users = userRepository.findAll(pageable);
        }
        return users.map(userMapper::toResponse);
    }
}
