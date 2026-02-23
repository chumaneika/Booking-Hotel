package com.booking_hotel.user_service.service;

import com.booking_hotel.user_service.dto.UserCreateDTO;
import com.booking_hotel.user_service.dto.UserResponseDTO;
import com.booking_hotel.user_service.dto.mapper.UserMapper;
import com.booking_hotel.user_service.entity.Status;
import com.booking_hotel.user_service.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
//@Primary
@RequiredArgsConstructor
public class UserServiceInMemory implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponseDTO createUser(UserCreateDTO dto) {
        UserEntity createdUser = userMapper.toEntity(dto);
        String hash = passwordEncoder.encode(dto.password());
        createdUser.changePassword(hash);

        return userMapper.toResponse(createdUser);
    }

    @Override
    public void deleteUser(Long userId) {

    }

    @Override
    public UserResponseDTO updateName(Long userId, String newName) {
        return null;
    }

    @Override
    public UserResponseDTO updateStatus(Long userId, Status status) {
        return null;
    }

    @Override
    public UserResponseDTO findUser(Long userId) {
        return null;
    }

    @Override
    public UserResponseDTO findUser(String email) {
        return null;
    }

    @Override
    public boolean checkActivityUser(Long userId) {
        return false;
    }

    @Override
    public Page<UserResponseDTO> getAllUsers(Status status, Pageable pageable) {
        return Page.empty();
    }
}
