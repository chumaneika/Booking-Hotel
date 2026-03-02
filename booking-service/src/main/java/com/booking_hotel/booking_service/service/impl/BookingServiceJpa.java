package com.booking_hotel.booking_service.service.impl;

import com.booking_hotel.booking_service.dto.bookingdto.BookingCreateRequestDTO;
import com.booking_hotel.booking_service.dto.bookingdto.BookingResponseDTO;
import com.booking_hotel.booking_service.dto.bookingdto.BookingStatusUpdateRequestDTO;
import com.booking_hotel.booking_service.dto.mapper.BookingRoomMapper;
import com.booking_hotel.booking_service.dto.mapper.BookingServiceMapper;
import com.booking_hotel.booking_service.dto.bookingroomdto.BookingRoomCreateRequestDTO;
import com.booking_hotel.booking_service.dto.bookingroomdto.BookingRoomResponseDTO;
import com.booking_hotel.booking_service.entity.BookingEntity;
import com.booking_hotel.booking_service.entity.BookingRoomEntity;
import com.booking_hotel.booking_service.entity.BookingStatus;
import com.booking_hotel.booking_service.repository.BookingRepository;
import com.booking_hotel.booking_service.service.BookingService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Primary
public class BookingServiceJpa implements BookingService {

    private final BookingRepository bookingRepository;
    private final BookingServiceMapper bookingServiceMapper;
    private final BookingRoomMapper bookingRoomMapper;

    @Override
    @Transactional
    public BookingResponseDTO createBooking(BookingCreateRequestDTO request) {
        validateBookingDates(request.checkInDate(), request.checkOutDate());

        BookingEntity booking = bookingServiceMapper.toEntity(request);
        List<BookingRoomEntity> roomEntities = booking.getRooms();

        for (BookingRoomCreateRequestDTO roomRequest : request.rooms()) {
            BookingRoomEntity room = bookingRoomMapper.toEntity(
                    roomRequest,
                    booking,
                    calculateRoomTotal(roomRequest.pricePerNight(), roomRequest.nights(), roomRequest.quantity())
            );
            roomEntities.add(room);
        }

        booking.updateTotalPrice(calculateBookingTotal(roomEntities));
        BookingEntity saved = bookingRepository.save(booking);
        return bookingServiceMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponseDTO getBookingByPublicId(UUID publicId) {
        BookingEntity booking = findByPublicIdOrThrow(publicId);
        return bookingServiceMapper.toResponseDTO(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDTO> getBookings(Long userId, Long hotelId, BookingStatus status, LocalDate checkIn, LocalDate checkOut) {
        Specification<BookingEntity> spec = buildFilterSpecification(userId, hotelId, status, checkIn, checkOut);
        return bookingRepository.findAll(spec).stream()
                .map(bookingServiceMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public BookingResponseDTO updateBookingStatus(UUID publicId, BookingStatusUpdateRequestDTO request) {
        BookingEntity booking = findByPublicIdOrThrow(publicId);
        booking.changeStatus(request.status());
        return bookingServiceMapper.toResponseDTO(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public void deleteBooking(UUID publicId) {
        BookingEntity booking = findByPublicIdOrThrow(publicId);
        bookingRepository.delete(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDTO> getBookingsByUserId(Long userId) {
        return bookingRepository.findAllByUserId(userId).stream()
                .map(bookingServiceMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDTO> getBookingsByHotelId(Long hotelId) {
        return bookingRepository.findAllByHotelId(hotelId).stream()
                .map(bookingServiceMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingRoomResponseDTO> getBookingRooms(UUID publicId) {
        BookingEntity booking = findByPublicIdOrThrow(publicId);
        return booking.getRooms().stream()
                .map(bookingRoomMapper::toResponseDTO)
                .toList();
    }

    private BookingEntity findByPublicIdOrThrow(UUID publicId) {
        return bookingRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found: " + publicId));
    }

    private void validateBookingDates(LocalDate checkInDate, LocalDate checkOutDate) {
        if (!checkOutDate.isAfter(checkInDate)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "checkOutDate must be after checkInDate");
        }
    }

    private BigDecimal calculateRoomTotal(BigDecimal pricePerNight, Integer nights, Integer quantity) {
        return pricePerNight
                .multiply(BigDecimal.valueOf(nights))
                .multiply(BigDecimal.valueOf(quantity));
    }

    private BigDecimal calculateBookingTotal(List<BookingRoomEntity> roomEntities) {
        return roomEntities.stream()
                .map(BookingRoomEntity::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Specification<BookingEntity> buildFilterSpecification(
            Long userId,
            Long hotelId,
            BookingStatus status,
            LocalDate checkIn,
            LocalDate checkOut
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (userId != null) {
                predicates.add(cb.equal(root.get("userId"), userId));
            }
            if (hotelId != null) {
                predicates.add(cb.equal(root.get("hotelId"), hotelId));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (checkIn != null) {
                predicates.add(cb.equal(root.get("checkInDate"), checkIn));
            }
            if (checkOut != null) {
                predicates.add(cb.equal(root.get("checkOutDate"), checkOut));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

}
