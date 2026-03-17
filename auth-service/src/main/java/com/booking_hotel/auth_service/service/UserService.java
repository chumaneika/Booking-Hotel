package com.booking_hotel.auth_service.service;

import com.booking_hotel.auth_service.dto.RegisterRequestDTO;
import com.booking_hotel.auth_service.entity.AccountEntity;

import java.time.Instant;

public interface UserService {
    AccountEntity createUser(RegisterRequestDTO dto);
    AccountEntity getByEmail(String email);
    AccountEntity getById(Long id);
    void rotateRefreshToken(Long accountId, String refreshToken, Instant refreshTokenExpiresAt);
    boolean isRefreshTokenValid(Long accountId, String refreshToken);
}
