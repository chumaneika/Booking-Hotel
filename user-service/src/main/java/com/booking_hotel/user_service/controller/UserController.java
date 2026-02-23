package com.booking_hotel.user_service.controller;

import com.booking_hotel.user_service.dto.UserCreateDTO;
import com.booking_hotel.user_service.dto.UserResponseDTO;
import com.booking_hotel.user_service.dto.UserUpdateNameDTO;
import com.booking_hotel.user_service.dto.UserUpdateStatusDTO;
import com.booking_hotel.user_service.entity.Status;
import com.booking_hotel.user_service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/create-user")
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody @Valid UserCreateDTO dto) {
        return ResponseEntity.ok(userService.createUser(dto));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/update-name")
    public ResponseEntity<UserResponseDTO> updateName(@RequestBody @Valid UserUpdateNameDTO dto) {
        return ResponseEntity.ok(userService.updateName(dto.userId(), dto.name()));
    }

    @PatchMapping("/update-status")
    public ResponseEntity<UserResponseDTO> updateStatus(@RequestBody @Valid UserUpdateStatusDTO dto) {
        return ResponseEntity.ok(userService.updateStatus(dto.userId(), dto.status()));
    }

    @GetMapping("/find/{identifier}")
    public ResponseEntity<UserResponseDTO> findUser(@PathVariable String identifier) {
        UserResponseDTO user;

        if (identifier.matches("\\d+")) {
            Long id = Long.parseLong(identifier);
            user = userService.findUser(id);
        } else {
            user = userService.findUser(identifier);
        }

        return ResponseEntity.ok(user);
    }

    @GetMapping("/by-email/{email}")
    public ResponseEntity<UserResponseDTO> getUserByEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.findUser(email));
    }

    @GetMapping("/{id}/check-activity")
    public ResponseEntity<Boolean> checkUserActivity(@PathVariable("id") Long userId) {
        return ResponseEntity.ok(userService.checkActivityUser(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable("id") Long userId) {
        return ResponseEntity.ok(userService.findUser(userId));
    }

    @GetMapping
    public ResponseEntity<Page<UserResponseDTO>> getAllUsers(
            @RequestParam(required = false) Status status,
            Pageable pageable) {
        return ResponseEntity.ok(userService.getAllUsers(status, pageable));
    }

}
