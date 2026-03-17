package com.booking_hotel.auth_service.service.impl;

import com.booking_hotel.auth_service.dto.AccountCreateDTO;
import com.booking_hotel.auth_service.dto.AccountResponseDTO;
import com.booking_hotel.auth_service.entity.AccountEntity;
import com.booking_hotel.auth_service.entity.Role;
import com.booking_hotel.auth_service.repository.UserRepository;
import com.booking_hotel.auth_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceJpa implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AccountResponseDTO createUser(AccountCreateDTO dto) {
        if (userRepository.findByEmail(dto.email()).isPresent()) {
            throw new BadCredentialsException("User with this email already exists");
        }

        String hash = passwordEncoder.encode(dto.password());
        AccountEntity account = new AccountEntity(
                UUID.randomUUID(),
                dto.email(),
                hash,
                Role.USER,
                true
        );
        AccountEntity createdUser = userRepository.save(account);

        return new AccountResponseDTO(
                createdUser.getId(),
                createdUser.getEmail(),
                createdUser.getRole()
        );
    }

}
