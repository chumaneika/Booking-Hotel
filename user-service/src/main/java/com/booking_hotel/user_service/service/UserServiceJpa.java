package com.booking_hotel.user_service.service;

import com.booking_hotel.user_service.dto.UserCreateRequestDTO;
import com.booking_hotel.user_service.dto.UserSummaryResponseDTO;
import com.booking_hotel.user_service.dto.UserUpdatePersonalInfoRequestDTO;
import com.booking_hotel.user_service.dto.UserUpdateStatusRequestDTO;
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
    public UserSummaryResponseDTO saveUser(UserCreateRequestDTO dto) {
        UserEntity entity = userRepository.save(userMapper.toEntity(dto));

        return userMapper.toResponse(entity);
    }

    @Override
    public void deleteUser(UUID userId) {
        UserEntity entity = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User is not found"));

        entity.changeStatus(Status.DELETED);
    }

    @Override
    public UserSummaryResponseDTO updatePersonalInfo(UserUpdatePersonalInfoRequestDTO dto) {
        UserEntity entity = userRepository.findById(dto.id())
                .orElseThrow(() -> new EntityNotFoundException("User is not found"));
        ensureUserIsNotDeleted(entity);

        entity.changePersonalInfo(dto.firstname(), dto.surname(), dto.birthday());
        userRepository.save(entity);

        return userMapper.toResponse(entity);

    }

    @Override
    public UserSummaryResponseDTO updateStatus(UserUpdateStatusRequestDTO dto) {
        UserEntity entity = userRepository.findById(dto.id())
                .orElseThrow(() -> new EntityNotFoundException("User is not found"));

        if (dto.status() == Status.DELETED) {
            throw new IllegalArgumentException("Use delete endpoint to mark user as deleted");
        }
        if (entity.getStatus() == Status.DELETED) {
            throw new IllegalStateException("Deleted user status cannot be changed");
        }

        entity.changeStatus(dto.status());
        userRepository.save(entity);

        return userMapper.toResponse(entity);
    }

    @Override
    public UserSummaryResponseDTO findUser(UUID userId) {
        UserEntity entity = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User is not found"));
        ensureUserIsNotDeleted(entity);

        return userMapper.toResponse(entity);
    }

    @Override
    public boolean checkActivityUser(UUID userId) {
        UserEntity entity = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User is not found"));

        return entity.getStatus().equals(Status.ACTIVE);
    }

    @Override
    public Page<UserSummaryResponseDTO> getUsers(Status status, Pageable pageable) {
        return userRepository
                .findByStatus(status, pageable)
                .map(userMapper::toResponse);
    }

    private void ensureUserIsNotDeleted(UserEntity entity) {
        if (entity.getStatus() == Status.DELETED) {
            throw new IllegalStateException("Deleted user is not available");
        }
    }
}
