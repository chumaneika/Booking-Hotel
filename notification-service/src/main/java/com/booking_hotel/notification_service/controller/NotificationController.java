package com.booking_hotel.notification_service.controller;

import com.booking_hotel.notification_service.dto.RegistrationSuccessNotificationRequest;
import com.booking_hotel.notification_service.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/hello")
    public ResponseEntity<String> testEndpoint() {
        return ResponseEntity.ok("Hello from Notification Service");
    }

    @PostMapping("/registration-success")
    public ResponseEntity<Void> sendRegistrationSuccessEmail(
            @RequestBody @Valid RegistrationSuccessNotificationRequest request
    ) {
        notificationService.sendRegistrationSuccessEmail(request);
        return ResponseEntity.ok().build();
    }
}
