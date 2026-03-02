package com.booking_hotel.booking_service.service.impl;

import com.booking_hotel.booking_service.dto.bookingdto.BookingCreateRequestDTO;
import com.booking_hotel.booking_service.dto.bookingdto.BookingResponseDTO;
import com.booking_hotel.booking_service.dto.bookingdto.BookingStatusUpdateRequestDTO;
import com.booking_hotel.booking_service.dto.bookingroomdto.BookingRoomResponseDTO;
import com.booking_hotel.booking_service.entity.BookingStatus;
import com.booking_hotel.booking_service.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingServiceInMemory implements BookingService {
    @Override
    public BookingResponseDTO createBooking(BookingCreateRequestDTO request) {
        throw new UnsupportedOperationException("BookingServiceInMemory is not implemented");
    }

    @Override
    public BookingResponseDTO getBookingByPublicId(UUID publicId) {
        throw new UnsupportedOperationException("BookingServiceInMemory is not implemented");
    }

    @Override
    public List<BookingResponseDTO> getBookings(Long userId, Long hotelId, BookingStatus status, LocalDate checkIn, LocalDate checkOut) {
        throw new UnsupportedOperationException("BookingServiceInMemory is not implemented");
    }

    @Override
    public BookingResponseDTO updateBookingStatus(UUID publicId, BookingStatusUpdateRequestDTO request) {
        throw new UnsupportedOperationException("BookingServiceInMemory is not implemented");
    }

    @Override
    public void deleteBooking(UUID publicId) {
        throw new UnsupportedOperationException("BookingServiceInMemory is not implemented");
    }

    @Override
    public List<BookingResponseDTO> getBookingsByUserId(Long userId) {
        throw new UnsupportedOperationException("BookingServiceInMemory is not implemented");
    }

    @Override
    public List<BookingResponseDTO> getBookingsByHotelId(Long hotelId) {
        throw new UnsupportedOperationException("BookingServiceInMemory is not implemented");
    }

    @Override
    public List<BookingRoomResponseDTO> getBookingRooms(UUID publicId) {
        throw new UnsupportedOperationException("BookingServiceInMemory is not implemented");
    }
}
