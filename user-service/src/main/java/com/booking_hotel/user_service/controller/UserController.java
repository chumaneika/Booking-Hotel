package com.booking_hotel.user_service.controller;

import com.booking_hotel.user_service.dto.UserCreateDTO;
import com.booking_hotel.user_service.dto.UserResponseDTO;
import com.booking_hotel.user_service.dto.UserUpdatePersonalInfoDTO;
import com.booking_hotel.user_service.dto.UserUpdateStatusDTO;
import com.booking_hotel.user_service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/create-user")
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody @Valid UserCreateDTO dto) {
        return null;
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") UUID userId) {
        return null;
    }

    @PatchMapping("/update-name")
    public ResponseEntity<UserResponseDTO> updateName(@RequestBody @Valid UserUpdatePersonalInfoDTO dto) {
        return null;
    }

    @PatchMapping("/update-status")
    public ResponseEntity<UserResponseDTO> updateStatus(@RequestBody @Valid UserUpdateStatusDTO dto) {
        return null;
    }

    @GetMapping("/find/{identifier}")
    public ResponseEntity<UserResponseDTO> findUser(@PathVariable String identifier) {
        return null;
    }

    @GetMapping("/by-email/{email}")
    public ResponseEntity<UserResponseDTO> getUserByEmail(@PathVariable String email) {
        return null;
    }

    @GetMapping("/{id}/check-activity")
    public ResponseEntity<Boolean> checkUserActivity(@PathVariable("id") UUID userId) {
        return null;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable("id") UUID userId) {
        return null;
    }

    @GetMapping
    public ResponseEntity<Page<UserResponseDTO>> getAllUsers() {
        return null;
    }

}
