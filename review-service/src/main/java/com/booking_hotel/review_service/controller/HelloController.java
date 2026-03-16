package com.booking_hotel.review_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("test/")
public class HelloController {

    @GetMapping("/hello")
    public ResponseEntity<String> testEndpoint() {
        return ResponseEntity.ok("Hello from Review Service");
    }

}
