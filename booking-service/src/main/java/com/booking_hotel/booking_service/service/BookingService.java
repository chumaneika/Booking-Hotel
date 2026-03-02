package com.booking_hotel.booking_service.service;

import com.booking_hotel.booking_service.dto.bookingdto.BookingCreateRequestDTO;
import com.booking_hotel.booking_service.dto.bookingdto.BookingResponseDTO;
import com.booking_hotel.booking_service.dto.bookingdto.BookingStatusUpdateRequestDTO;
import com.booking_hotel.booking_service.dto.bookingroomdto.BookingRoomResponseDTO;
import com.booking_hotel.booking_service.entity.BookingStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface BookingService {
    BookingResponseDTO createBooking(BookingCreateRequestDTO request);
    BookingResponseDTO getBookingByPublicId(UUID publicId);
    List<BookingResponseDTO> getBookings(Long userId, Long hotelId, BookingStatus status, LocalDate checkIn, LocalDate checkOut);
    BookingResponseDTO updateBookingStatus(UUID publicId, BookingStatusUpdateRequestDTO request);
    void deleteBooking(UUID publicId);
    List<BookingResponseDTO> getBookingsByUserId(Long userId);
    List<BookingResponseDTO> getBookingsByHotelId(Long hotelId);
    List<BookingRoomResponseDTO> getBookingRooms(UUID publicId);
}
