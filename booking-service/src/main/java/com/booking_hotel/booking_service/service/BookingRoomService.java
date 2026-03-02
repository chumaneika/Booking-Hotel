package com.booking_hotel.booking_service.service;

import com.booking_hotel.booking_service.dto.bookingroomdto.BookingRoomCreateForBookingRequestDTO;
import com.booking_hotel.booking_service.dto.bookingroomdto.BookingRoomQuantityUpdateRequestDTO;
import com.booking_hotel.booking_service.dto.bookingroomdto.BookingRoomResponseDTO;

import java.util.List;
import java.util.UUID;

public interface BookingRoomService {
    BookingRoomResponseDTO createBookingRoom(BookingRoomCreateForBookingRequestDTO request);
    BookingRoomResponseDTO getBookingRoomById(Long id);
    BookingRoomResponseDTO updateBookingRoomQuantity(Long id, BookingRoomQuantityUpdateRequestDTO request);
    void deleteBookingRoom(Long id);
    List<BookingRoomResponseDTO> getBookingRoomsByBookingPublicId(UUID bookingPublicId);
}
