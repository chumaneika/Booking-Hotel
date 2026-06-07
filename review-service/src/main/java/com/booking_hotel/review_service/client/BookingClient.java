package com.booking_hotel.review_service.client;

import com.booking_hotel.review_service.client.dto.BookingDetailsDTO;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BookingClient {

    private final RestClient.Builder restClientBuilder;

    @Value("${booking-service.url:http://localhost:8083}")
    private String bookingServiceUrl;

    public BookingDetailsDTO getBooking(UUID bookingPublicId) {
        try {
            return restClientBuilder
                    .baseUrl(bookingServiceUrl)
                    .build()
                    .get()
                    .uri("/api/bookings/{publicId}", bookingPublicId)
                    .headers(this::forwardAuthorizationHeader)
                    .retrieve()
                    .body(BookingDetailsDTO.class);
        } catch (RestClientResponseException exception) {
            if (exception.getStatusCode().isSameCodeAs(HttpStatus.NOT_FOUND)) {
                throw new EntityNotFoundException("Booking is not found");
            }
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Failed to fetch booking from booking-service",
                    exception
            );
        } catch (RestClientException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Failed to fetch booking from booking-service",
                    exception
            );
        }
    }

    private void forwardAuthorizationHeader(HttpHeaders headers) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return;
        }

        HttpServletRequest request = attributes.getRequest();
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorization != null && !authorization.isBlank()) {
            headers.set(HttpHeaders.AUTHORIZATION, authorization);
        }
    }
}
