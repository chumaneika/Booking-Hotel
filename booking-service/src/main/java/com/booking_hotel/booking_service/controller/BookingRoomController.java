package com.booking_hotel.booking_service.controller;

import com.booking_hotel.booking_service.dto.bookingroomdto.BookingRoomCreateForBookingRequestDTO;
import com.booking_hotel.booking_service.dto.bookingroomdto.BookingRoomQuantityUpdateRequestDTO;
import com.booking_hotel.booking_service.dto.bookingroomdto.BookingRoomResponseDTO;
import com.booking_hotel.booking_service.service.BookingRoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;


@RestController
@RequiredArgsConstructor
@RequestMapping("api/booking-rooms")
public class BookingRoomController {

    private final BookingRoomService bookingRoomService;

    @PostMapping
    public ResponseEntity<BookingRoomResponseDTO> createBookingRoom(
            @Valid @RequestBody BookingRoomCreateForBookingRequestDTO request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingRoomService.createBookingRoom(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingRoomResponseDTO> getBookingRoom(@PathVariable Long id) {
        return ResponseEntity.ok(bookingRoomService.getBookingRoomById(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<BookingRoomResponseDTO> updateBookingRoomQuantity(
            @PathVariable Long id,
            @Valid @RequestBody BookingRoomQuantityUpdateRequestDTO request
    ) {
        return ResponseEntity.ok(bookingRoomService.updateBookingRoomQuantity(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBookingRoom(@PathVariable Long id) {
        bookingRoomService.deleteBookingRoom(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/booking/{bookingPublicId}")
    public ResponseEntity<List<BookingRoomResponseDTO>> getBookingRoomsByBookingPublicId(
            @PathVariable UUID bookingPublicId
    ) {
        return ResponseEntity.ok(bookingRoomService.getBookingRoomsByBookingPublicId(bookingPublicId));
    }
}
