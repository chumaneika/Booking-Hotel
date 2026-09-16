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
import com.booking_hotel.booking_service.kafka.publisher.BookingEventPublisher;
import com.booking_hotel.booking_service.repository.BookingRepository;
import com.booking_hotel.booking_service.security.BookingAccessGuard;
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
    private final BookingAccessGuard bookingAccessGuard;
    private final BookingEventPublisher bookingEventPublisher;
    private final com.booking_hotel.booking_service.intergration.client.CatalogRoomTypeClient catalogRoomTypeClient;

    @Override
    @Transactional
    public BookingResponseDTO createBooking(BookingCreateRequestDTO request) {
        bookingAccessGuard.ensureCurrentUserOrPrivileged(request.userId());
        validateBookingDates(request.checkInDate(), request.checkOutDate());

        BookingEntity booking = bookingServiceMapper.toEntity(request);
        List<BookingRoomEntity> roomEntities = booking.getRooms();
        int nights = Math.toIntExact(java.time.temporal.ChronoUnit.DAYS.between(request.checkInDate(), request.checkOutDate()));
        java.util.Set<Long> roomIds = new java.util.HashSet<>();

        for (BookingRoomCreateRequestDTO roomRequest : request.rooms()) {
            if (!roomIds.add(roomRequest.roomTypeId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Duplicate room type");
            }
            var details = catalogRoomTypeClient.getRoomType(request.hotelId(), roomRequest.roomTypeId());
            if (details == null || details.basePrice() == null || details.basePrice().signum() <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Invalid catalog price");
            }
            BigDecimal price = details.basePrice();
            BookingRoomEntity room = new BookingRoomEntity(booking, roomRequest.roomTypeId(),
                    roomRequest.quantity(), price, nights, calculateRoomTotal(price, nights, roomRequest.quantity()));
            roomEntities.add(room);
        }

        booking.updateTotalPrice(calculateBookingTotal(roomEntities));
        BookingEntity saved = bookingRepository.save(booking);
        bookingEventPublisher.publishBookingCreated(saved);
        bookingEventPublisher.publishRoomReservationRequested(saved);
        return bookingServiceMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponseDTO getBookingByPublicId(UUID publicId) {
        BookingEntity booking = findByPublicIdOrThrow(publicId);
        bookingAccessGuard.ensureCanAccessBooking(booking);
        return bookingServiceMapper.toResponseDTO(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDTO> getBookings(Long userId, Long hotelId, BookingStatus status, LocalDate checkIn, LocalDate checkOut) {
        Long effectiveUserId = bookingAccessGuard.constrainUserFilter(userId);
        Specification<BookingEntity> spec = buildFilterSpecification(effectiveUserId, hotelId, status, checkIn, checkOut);
        return bookingRepository.findAll(spec).stream()
                .map(bookingServiceMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public BookingResponseDTO updateBookingStatus(UUID publicId, BookingStatusUpdateRequestDTO request) {
        bookingAccessGuard.ensurePrivileged();
        BookingEntity booking = findByPublicIdOrThrow(publicId);
        BookingStatus previousStatus = booking.getStatus();
        if (previousStatus == request.status()) return bookingServiceMapper.toResponseDTO(booking);
        if (!(request.status() == BookingStatus.CONFIRMED && previousStatus == BookingStatus.PAID)
                && !((request.status() == BookingStatus.CANCELLED || request.status() == BookingStatus.EXPIRED)
                && previousStatus != BookingStatus.PAID && previousStatus != BookingStatus.CONFIRMED
                && previousStatus != BookingStatus.CANCELLED && previousStatus != BookingStatus.EXPIRED)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Invalid booking status transition");
        }
        booking.changeStatus(request.status());
        BookingEntity saved = bookingRepository.save(booking);
        publishStatusEvent(saved, "Status updated by a privileged user");
        if (request.status() == BookingStatus.CANCELLED || request.status() == BookingStatus.EXPIRED) {
            bookingEventPublisher.publishRoomReservationReleased(saved, "Booking cancelled");
        }
        return bookingServiceMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional
    public void deleteBooking(UUID publicId) {
        BookingEntity booking = findByPublicIdOrThrow(publicId);
        bookingAccessGuard.ensureCanAccessBooking(booking);
        if (booking.getStatus() == BookingStatus.PAID || booking.getStatus() == BookingStatus.CONFIRMED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Paid booking requires a refund workflow");
        }
        if (booking.getStatus() != BookingStatus.CANCELLED && booking.getStatus() != BookingStatus.EXPIRED) {
            booking.changeStatus(BookingStatus.CANCELLED);
            bookingEventPublisher.publishBookingCancelled(booking, "Cancelled by owner");
            bookingEventPublisher.publishRoomReservationReleased(booking, "Cancelled by owner");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDTO> getBookingsByUserId(Long userId) {
        Long effectiveUserId = bookingAccessGuard.constrainUserFilter(userId);
        return bookingRepository.findAllByUserId(effectiveUserId).stream()
                .map(bookingServiceMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDTO> getBookingsByHotelId(Long hotelId) {
        if (bookingAccessGuard.isPrivileged()) {
            return bookingRepository.findAllByHotelId(hotelId).stream()
                    .map(bookingServiceMapper::toResponseDTO)
                    .toList();
        }

        return getBookings(bookingAccessGuard.currentUserId(), hotelId, null, null, null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingRoomResponseDTO> getBookingRooms(UUID publicId) {
        BookingEntity booking = findByPublicIdOrThrow(publicId);
        bookingAccessGuard.ensureCanAccessBooking(booking);
        return booking.getRooms().stream()
                .map(bookingRoomMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public void handleRoomsReserved(UUID bookingPublicId) {
        BookingEntity booking = findByPublicIdOrThrow(bookingPublicId);
        if (booking.getStatus() == BookingStatus.NEW) {
            booking.changeStatus(BookingStatus.PAYMENT_PENDING);
            BookingEntity saved = bookingRepository.save(booking);
            bookingEventPublisher.publishPaymentRequested(saved);
        }
    }

    @Override
    @Transactional
    public void handleRoomReservationRejected(UUID bookingPublicId, String reason) {
        BookingEntity booking = findByPublicIdOrThrow(bookingPublicId);
        if (booking.getStatus() == BookingStatus.NEW
                || booking.getStatus() == BookingStatus.ROOM_RESERVED
                || booking.getStatus() == BookingStatus.PAYMENT_PENDING) {
            booking.changeStatus(BookingStatus.CANCELLED);
            BookingEntity saved = bookingRepository.save(booking);
            bookingEventPublisher.publishBookingCancelled(saved, reason);
        }
    }

    @Override
    @Transactional
    public void handlePaymentSucceeded(UUID bookingPublicId) {
        BookingEntity booking = findByPublicIdOrThrow(bookingPublicId);
        if (booking.getStatus() == BookingStatus.ROOM_RESERVED
                || booking.getStatus() == BookingStatus.PAYMENT_PENDING) {
            booking.changeStatus(BookingStatus.PAID);
            bookingRepository.save(booking);
        }
    }

    @Override
    @Transactional
    public void handlePaymentFailed(UUID bookingPublicId, String reason) {
        BookingEntity booking = findByPublicIdOrThrow(bookingPublicId);
        if (booking.getStatus() == BookingStatus.ROOM_RESERVED
                || booking.getStatus() == BookingStatus.PAYMENT_PENDING) {
            booking.changeStatus(BookingStatus.CANCELLED);
            BookingEntity saved = bookingRepository.save(booking);
            bookingEventPublisher.publishBookingCancelled(saved, reason);
            bookingEventPublisher.publishRoomReservationReleased(saved, reason);
        }
    }


    private void publishStatusEvent(BookingEntity booking, String cancellationReason) {
        switch (booking.getStatus()) {
            case CONFIRMED -> bookingEventPublisher.publishBookingConfirmed(booking);
            case CANCELLED -> bookingEventPublisher.publishBookingCancelled(booking, cancellationReason);
            case EXPIRED -> bookingEventPublisher.publishBookingExpired(booking);
            default -> {
                // Only business-significant terminal status transitions are published here.
            }
        }
    }

    private boolean isRoomReserved(BookingStatus status) {
        return status == BookingStatus.ROOM_RESERVED || status == BookingStatus.PAYMENT_PENDING;
    }

    private BookingEntity findByPublicIdOrThrow(UUID publicId) {
        return (org.springframework.transaction.support.TransactionSynchronizationManager.isCurrentTransactionReadOnly()
                ? bookingRepository.findByPublicId(publicId) : bookingRepository.findLockedByPublicId(publicId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found: " + publicId));
    }

    private void validateBookingDates(LocalDate checkInDate, LocalDate checkOutDate) {
        if (!checkOutDate.isAfter(checkInDate) || checkInDate.isBefore(LocalDate.now())
                || java.time.temporal.ChronoUnit.DAYS.between(checkInDate, checkOutDate) > 365) {
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
