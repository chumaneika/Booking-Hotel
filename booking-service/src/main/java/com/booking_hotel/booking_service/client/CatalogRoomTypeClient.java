package com.booking_hotel.booking_service.client;

import com.booking_hotel.booking_service.client.dto.RoomTypeDetailsDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

@Component
@RequiredArgsConstructor
public class CatalogRoomTypeClient {

    private final RestClient.Builder restClientBuilder;

    @Value("${catalog-service.url:http://localhost:8082}")
    private String catalogServiceUrl;

    public RoomTypeDetailsDTO getRoomType(Long hotelId, Long roomTypeId) {
        try {
            return restClientBuilder
                    .baseUrl(catalogServiceUrl)
                    .build()
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/room-types/{roomTypeId}")
                            .queryParam("hotelId", hotelId)
                            .build(roomTypeId)
                    )
                    .headers(this::forwardAuthorizationHeader)
                    .retrieve()
                    .body(RoomTypeDetailsDTO.class);
        } catch (RestClientException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Failed to fetch room type from catalog-service",
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
