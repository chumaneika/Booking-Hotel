package com.booking_hotel.auth_service.service.impl;

import com.booking_hotel.auth_service.dto.RegisterRequestDTO;
import com.booking_hotel.auth_service.entity.AccountEntity;
import com.booking_hotel.auth_service.entity.Role;
import com.booking_hotel.auth_service.repository.UserRepository;
import com.booking_hotel.auth_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HexFormat;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceJpa implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AccountEntity createUser(RegisterRequestDTO dto) {
        if (userRepository.findByEmail(dto.email()).isPresent()) {
            throw new IllegalArgumentException("Account with email already exists");
        }

        AccountEntity createdUser = new AccountEntity(
                UUID.randomUUID(),
                dto.email(),
                passwordEncoder.encode(dto.password()),
                Role.USER,
                true
        );

        return userRepository.save(createdUser);
    }

    @Override
    public AccountEntity getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with email: " + email));
    }

    @Override
    public AccountEntity getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with id: " + id));
    }

    @Override
    public void rotateRefreshToken(Long accountId, String refreshToken, Instant refreshTokenExpiresAt) {
        AccountEntity account = getById(accountId);
        account.rotateRefreshToken(hashToken(refreshToken), refreshTokenExpiresAt);
        userRepository.save(account);
    }

    @Override
    public boolean isRefreshTokenValid(Long accountId, String refreshToken) {
        AccountEntity account = getById(accountId);
        if (account.getRefreshTokenHash() == null || account.getRefreshTokenExpiresAt() == null) {
            return false;
        }
        if (account.getRefreshTokenExpiresAt().isBefore(Instant.now())) {
            return false;
        }
        return account.getRefreshTokenHash().equals(hashToken(refreshToken));
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(bytes);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to hash refresh token", e);
        }
    }
}
