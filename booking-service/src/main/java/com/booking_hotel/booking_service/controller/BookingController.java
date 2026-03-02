package com.booking_hotel.booking_service.controller;

import com.booking_hotel.booking_service.dto.bookingdto.BookingCreateRequestDTO;
import com.booking_hotel.booking_service.dto.bookingdto.BookingResponseDTO;
import com.booking_hotel.booking_service.dto.bookingdto.BookingStatusUpdateRequestDTO;
import com.booking_hotel.booking_service.dto.bookingroomdto.BookingRoomResponseDTO;
import com.booking_hotel.booking_service.entity.BookingStatus;
import jakarta.validation.Valid;
import com.booking_hotel.booking_service.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponseDTO> createBooking(@Valid @RequestBody BookingCreateRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.createBooking(request));
    }

    @GetMapping("/{publicId}")
    public ResponseEntity<BookingResponseDTO> getBooking(@PathVariable UUID publicId) {
        return ResponseEntity.ok(bookingService.getBookingByPublicId(publicId));
    }

    @GetMapping
    public ResponseEntity<List<BookingResponseDTO>> getBookings(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long hotelId,
            @RequestParam(required = false) BookingStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut
    ) {
        return ResponseEntity.ok(bookingService.getBookings(userId, hotelId, status, checkIn, checkOut));
    }

    @PatchMapping("/{publicId}/status")
    public ResponseEntity<BookingResponseDTO> updateStatus(
            @PathVariable UUID publicId,
            @Valid @RequestBody BookingStatusUpdateRequestDTO request
    ) {
        return ResponseEntity.ok(bookingService.updateBookingStatus(publicId, request));
    }

    @DeleteMapping("/{publicId}")
    public ResponseEntity<Void> deleteBooking(@PathVariable UUID publicId) {
        bookingService.deleteBooking(publicId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BookingResponseDTO>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(bookingService.getBookingsByUserId(userId));
    }

    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<BookingResponseDTO>> getByHotel(@PathVariable Long hotelId) {
        return ResponseEntity.ok(bookingService.getBookingsByHotelId(hotelId));
    }

    @GetMapping("/{publicId}/rooms")
    public ResponseEntity<List<BookingRoomResponseDTO>> getBookingRooms(@PathVariable UUID publicId) {
        return ResponseEntity.ok(bookingService.getBookingRooms(publicId));
    }
}
