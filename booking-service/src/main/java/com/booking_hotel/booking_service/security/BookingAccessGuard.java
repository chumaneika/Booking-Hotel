package com.booking_hotel.booking_service.security;

import com.booking_hotel.booking_service.entity.BookingEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

@Component
public class BookingAccessGuard {

    private static final Set<String> PRIVILEGED_ROLES = Set.of("ROLE_ADMIN", "ROLE_MANAGER", "ROLE_HOTEL_OWNER");

    public Long currentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getDetails() instanceof Long userId) {
            return userId;
        }

        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Authenticated user id is missing");
    }

    public boolean isPrivileged() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }

        return authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(PRIVILEGED_ROLES::contains);
    }

    public void ensurePrivileged() {
        if (!isPrivileged()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
    }

    public void ensureCurrentUserOrPrivileged(Long userId) {
        if (!isPrivileged() && !currentUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
    }

    public void ensureCanAccessBooking(BookingEntity booking) {
        ensureCurrentUserOrPrivileged(booking.getUserId());
    }

    public Long constrainUserFilter(Long requestedUserId) {
        if (isPrivileged()) {
            return requestedUserId;
        }

        Long currentUserId = currentUserId();
        if (requestedUserId != null && !currentUserId.equals(requestedUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        return currentUserId;
    }
}
