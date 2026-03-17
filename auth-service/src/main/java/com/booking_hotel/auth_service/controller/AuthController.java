package com.booking_hotel.auth_service.controller;


import com.booking_hotel.auth_service.dto.*;
import com.booking_hotel.auth_service.entity.AccountEntity;
import com.booking_hotel.auth_service.security.AccountDetailsServiceImpl;
import com.booking_hotel.auth_service.security.JwtService;
import com.booking_hotel.auth_service.security.AccountDetailsImpl;
import com.booking_hotel.auth_service.service.UserService;
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
    private final AccountDetailsServiceImpl userDetailsService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@RequestBody @Valid RegisterRequestDTO request) {
        AccountEntity createdUser = userService.createUser(request);
        UserDetails userDetails = userDetailsService.loadUserByUsername(createdUser.getEmail());
        AccountDetailsImpl userDetailsImpl = (AccountDetailsImpl) userDetails;

        String token = jwtService.generateAccessToken(userDetailsImpl, createdUser.getId());
        String refreshToken = jwtService.generateRefreshToken(userDetailsImpl, createdUser.getId());
        userService.rotateRefreshToken(
                createdUser.getId(),
                refreshToken,
                jwtService.extractExpirationInstant(refreshToken)
        );

        return ResponseEntity.ok(new AuthResponseDTO(
                token,
                refreshToken,
                createdUser.getId(),
                createdUser.getEmail(),
                request.firstname(),
                request.surname()
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody @Valid LoginRequestDTO request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        AccountDetailsImpl userDetails = (AccountDetailsImpl) authentication.getPrincipal();

        String token = jwtService.generateAccessToken(userDetails, userDetails.getId());
        String refreshToken = jwtService.generateRefreshToken(userDetails, userDetails.getId());
        userService.rotateRefreshToken(
                userDetails.getId(),
                refreshToken,
                jwtService.extractExpirationInstant(refreshToken)
        );

        return ResponseEntity.ok(new AuthResponseDTO(
                token,
                refreshToken,
                userDetails.getId(),
                userDetails.getUsername(),
                null,
                null
        ));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDTO> refresh(@RequestBody @Valid RefreshTokenRequestDTO request) {
        String refreshToken = request.refreshToken();
        String email = jwtService.extractEmail(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        AccountDetailsImpl details = (AccountDetailsImpl) userDetails;

        if (!jwtService.validateRefreshToken(refreshToken, details)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }
        if (!userService.isRefreshTokenValid(details.getId(), refreshToken)) {
            throw new IllegalArgumentException("Refresh token is expired or revoked");
        }

        String newAccessToken = jwtService.generateAccessToken(details, details.getId());
        String newRefreshToken = jwtService.generateRefreshToken(details, details.getId());
        userService.rotateRefreshToken(
                details.getId(),
                newRefreshToken,
                jwtService.extractExpirationInstant(newRefreshToken)
        );

        return ResponseEntity.ok(new AuthResponseDTO(
                newAccessToken,
                newRefreshToken,
                details.getId(),
                details.getUsername(),
                null,
                null
        ));
    }
}
