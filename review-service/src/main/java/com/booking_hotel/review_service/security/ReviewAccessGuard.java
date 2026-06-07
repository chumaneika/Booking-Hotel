package com.booking_hotel.review_service.security;

import com.booking_hotel.review_service.entity.ReviewEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

@Component
public class ReviewAccessGuard {

    private static final Set<String> PRIVILEGED_ROLES = Set.of("ROLE_ADMIN", "ROLE_MANAGER");

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

    public void ensureCurrentUserOrPrivileged(Long userId) {
        if (!isPrivileged() && !currentUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
    }

    public void ensureCanChangeReview(ReviewEntity review) {
        ensureCurrentUserOrPrivileged(review.getUserId());
    }
}
