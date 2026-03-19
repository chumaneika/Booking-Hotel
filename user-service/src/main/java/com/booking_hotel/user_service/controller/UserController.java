package com.booking_hotel.user_service.controller;

import com.booking_hotel.user_service.dto.UserCreateRequestDTO;
import com.booking_hotel.user_service.dto.UserSummaryResponseDTO;
import com.booking_hotel.user_service.dto.UserUpdatePersonalInfoRequestDTO;
import com.booking_hotel.user_service.dto.UserUpdateStatusRequestDTO;
import com.booking_hotel.user_service.entity.Status;
import com.booking_hotel.user_service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/create-user")
    public ResponseEntity<UserSummaryResponseDTO> saveUser(@RequestBody @Valid UserCreateRequestDTO dto) {
        UserSummaryResponseDTO responseBody = userService.saveUser(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") UUID userId) {
        userService.deleteUser(userId);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/update-name")
    public ResponseEntity<UserSummaryResponseDTO> updatePersonalInfo(@RequestBody @Valid UserUpdatePersonalInfoRequestDTO dto) {
        UserSummaryResponseDTO response = userService.updatePersonalInfo(dto);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/update-status")
    public ResponseEntity<UserSummaryResponseDTO> updateStatus(@RequestBody @Valid UserUpdateStatusRequestDTO dto) {
        UserSummaryResponseDTO response = userService.updateStatus(dto);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<UserSummaryResponseDTO> findUserById(@PathVariable("id") UUID userId) {
        UserSummaryResponseDTO response = userService.findUser(userId);

        return ResponseEntity.status(HttpStatus.FOUND).body(response);
    }

    @GetMapping("/check-activity/{id}")
    public ResponseEntity<Boolean> checkUserActivity(@PathVariable("id") UUID userId) {
        boolean response = userService.checkActivityUser(userId);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<UserSummaryResponseDTO>> getUsers(@RequestParam Status status, Pageable pageable) {
        Page<UserSummaryResponseDTO> response = userService.getUsers(status, pageable);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
