package com.booking_hotel.booking_service.service.impl;

import com.booking_hotel.booking_service.dto.bookingroomdto.BookingRoomCreateForBookingRequestDTO;
import com.booking_hotel.booking_service.dto.bookingroomdto.BookingRoomQuantityUpdateRequestDTO;
import com.booking_hotel.booking_service.dto.bookingroomdto.BookingRoomResponseDTO;
import com.booking_hotel.booking_service.service.BookingRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingRoomServiceInMemory implements BookingRoomService {
    @Override
    public BookingRoomResponseDTO createBookingRoom(BookingRoomCreateForBookingRequestDTO request) {
        throw new UnsupportedOperationException("BookingRoomServiceInMemory is not implemented");
    }

    @Override
    public BookingRoomResponseDTO getBookingRoomById(Long id) {
        throw new UnsupportedOperationException("BookingRoomServiceInMemory is not implemented");
    }

    @Override
    public BookingRoomResponseDTO updateBookingRoomQuantity(Long id, BookingRoomQuantityUpdateRequestDTO request) {
        throw new UnsupportedOperationException("BookingRoomServiceInMemory is not implemented");
    }

    @Override
    public void deleteBookingRoom(Long id) {
        throw new UnsupportedOperationException("BookingRoomServiceInMemory is not implemented");
    }

    @Override
    public List<BookingRoomResponseDTO> getBookingRoomsByBookingPublicId(UUID bookingPublicId) {
        throw new UnsupportedOperationException("BookingRoomServiceInMemory is not implemented");
    }
}
