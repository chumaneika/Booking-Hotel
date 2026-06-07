package com.booking_hotel.booking_service.service.impl;

import com.booking_hotel.booking_service.client.CatalogRoomTypeClient;
import com.booking_hotel.booking_service.client.dto.RoomTypeDetailsDTO;
import com.booking_hotel.booking_service.dto.bookingroomdto.BookingRoomCreateForBookingRequestDTO;
import com.booking_hotel.booking_service.dto.bookingroomdto.BookingRoomQuantityUpdateRequestDTO;
import com.booking_hotel.booking_service.dto.bookingroomdto.BookingRoomResponseDTO;
import com.booking_hotel.booking_service.dto.mapper.BookingRoomMapper;
import com.booking_hotel.booking_service.entity.BookingEntity;
import com.booking_hotel.booking_service.entity.BookingRoomEntity;
import com.booking_hotel.booking_service.entity.BookingStatus;
import com.booking_hotel.booking_service.repository.BookingRepository;
import com.booking_hotel.booking_service.repository.BookingRoomRepository;
import com.booking_hotel.booking_service.security.BookingAccessGuard;
import com.booking_hotel.booking_service.service.BookingRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Primary
public class BookingRoomServiceJpa implements BookingRoomService {
    private static final Set<BookingStatus> LOCKED_BOOKING_STATUSES = EnumSet.of(
            BookingStatus.PAYMENT_PENDING,
            BookingStatus.PAID,
            BookingStatus.CONFIRMED,
            BookingStatus.CANCELLED,
            BookingStatus.EXPIRED
    );

    private final BookingRoomRepository bookingRoomRepository;
    private final BookingRepository bookingRepository;
    private final BookingRoomMapper bookingRoomMapper;
    private final CatalogRoomTypeClient catalogRoomTypeClient;
    private final BookingAccessGuard bookingAccessGuard;

    @Override
    @Transactional
    public BookingRoomResponseDTO createBookingRoom(BookingRoomCreateForBookingRequestDTO request) {
        BookingEntity booking = findBookingByPublicIdOrThrow(request.bookingPublicId());
        bookingAccessGuard.ensureCanAccessBooking(booking);
        ensureBookingRoomsCanBeChanged(booking);

        RoomTypeDetailsDTO roomType = catalogRoomTypeClient.getRoomType(booking.getHotelId(), request.roomTypeId());
        Integer nights = calculateNights(booking);
        BigDecimal pricePerNight = roomType.basePrice();

        BookingRoomEntity room = bookingRoomMapper.toEntity(
                request,
                booking,
                pricePerNight,
                nights,
                calculateRoomTotal(pricePerNight, nights, request.quantity())
        );

        booking.getRooms().add(room);
        BookingRoomEntity saved = bookingRoomRepository.save(room);
        updateBookingTotalPrice(booking);
        return bookingRoomMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingRoomResponseDTO getBookingRoomById(Long id) {
        BookingRoomEntity room = findRoomByIdOrThrow(id);
        bookingAccessGuard.ensureCanAccessBooking(room.getBooking());
        return bookingRoomMapper.toResponseDTO(room);
    }

    @Override
    @Transactional
    public BookingRoomResponseDTO updateBookingRoomQuantity(Long id, BookingRoomQuantityUpdateRequestDTO request) {
        BookingRoomEntity room = findRoomByIdOrThrow(id);
        bookingAccessGuard.ensureCanAccessBooking(room.getBooking());
        ensureBookingRoomsCanBeChanged(room.getBooking());

        room.changeQuantity(request.quantity());
        BookingRoomEntity saved = bookingRoomRepository.save(room);
        updateBookingTotalPrice(room.getBooking());
        return bookingRoomMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional
    public void deleteBookingRoom(Long id) {
        BookingRoomEntity room = findRoomByIdOrThrow(id);
        BookingEntity booking = room.getBooking();
        bookingAccessGuard.ensureCanAccessBooking(booking);
        ensureBookingRoomsCanBeChanged(booking);

        booking.getRooms().remove(room);
        bookingRoomRepository.delete(room);
        updateBookingTotalPrice(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingRoomResponseDTO> getBookingRoomsByBookingPublicId(UUID bookingPublicId) {
        BookingEntity booking = findBookingByPublicIdOrThrow(bookingPublicId);
        bookingAccessGuard.ensureCanAccessBooking(booking);
        return bookingRoomRepository.findAllByBookingPublicId(bookingPublicId).stream()
                .map(bookingRoomMapper::toResponseDTO)
                .toList();
    }

    private BookingEntity findBookingByPublicIdOrThrow(UUID publicId) {
        return bookingRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found: " + publicId));
    }

    private BookingRoomEntity findRoomByIdOrThrow(Long id) {
        return bookingRoomRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking room not found: " + id));
    }

    private void updateBookingTotalPrice(BookingEntity booking) {
        BigDecimal total = booking.getRooms().stream()
                .map(BookingRoomEntity::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        booking.updateTotalPrice(total);
        bookingRepository.save(booking);
    }

    private BigDecimal calculateRoomTotal(BigDecimal pricePerNight, Integer nights, Integer quantity) {
        return pricePerNight
                .multiply(BigDecimal.valueOf(nights))
                .multiply(BigDecimal.valueOf(quantity));
    }

    private Integer calculateNights(BookingEntity booking) {
        return Math.toIntExact(ChronoUnit.DAYS.between(booking.getCheckInDate(), booking.getCheckOutDate()));
    }

    private void ensureBookingRoomsCanBeChanged(BookingEntity booking) {
        if (LOCKED_BOOKING_STATUSES.contains(booking.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Booking rooms cannot be changed when booking status is " + booking.getStatus()
            );
        }
    }

}
