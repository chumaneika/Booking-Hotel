package com.booking_hotel.auth_service.service;

import com.booking_hotel.auth_service.dto.AccountCreateDTO;
import com.booking_hotel.auth_service.dto.AccountResponseDTO;

public interface UserService {
    AccountResponseDTO createUser(AccountCreateDTO dto);
}
