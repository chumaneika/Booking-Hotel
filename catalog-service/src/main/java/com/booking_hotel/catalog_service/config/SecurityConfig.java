package com.booking_hotel.catalog_service.config;

import com.booking_hotel.catalog_service.security.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/hotels/**", "/api/room-types/**").hasAnyRole("ADMIN", "HOTEL_OWNER", "MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/api/hotels/**", "/api/room-types/**").hasAnyRole("ADMIN", "HOTEL_OWNER", "MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/api/hotels/**", "/api/room-types/**").hasAnyRole("ADMIN", "HOTEL_OWNER", "MANAGER")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

}
