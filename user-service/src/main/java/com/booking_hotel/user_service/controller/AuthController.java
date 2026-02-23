package com.booking_hotel.user_service.controller;

import com.booking_hotel.user_service.dto.AuthResponse;
import com.booking_hotel.user_service.dto.LoginRequest;
import com.booking_hotel.user_service.dto.RegisterRequest;
import com.booking_hotel.user_service.dto.UserCreateDTO;
import com.booking_hotel.user_service.dto.UserResponseDTO;
import com.booking_hotel.user_service.security.JwtService;
import com.booking_hotel.user_service.security.UserDetailsImpl;
import com.booking_hotel.user_service.security.UserDetailsServiceImpl;
import com.booking_hotel.user_service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;
    private final UserDetailsServiceImpl userDetailsService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody @Valid RegisterRequest request) {
        UserCreateDTO createDTO = new UserCreateDTO(
                request.name(),
                request.password(),
                request.email()
        );

        UserResponseDTO userResponse = userService.createUser(createDTO);

        // Load user details for token generation
        UserDetails userDetails = userDetailsService.loadUserByUsername(userResponse.email());
        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) userDetails;

        String token = jwtService.generateToken(userDetailsImpl, userResponse.id());

        return ResponseEntity.ok(new AuthResponse(
                token,
                userResponse.id(),
                userResponse.email(),
                userResponse.name()
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String token = jwtService.generateToken(userDetails, userDetails.getId());

        return ResponseEntity.ok(new AuthResponse(
                token,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getName()
        ));
    }
}
