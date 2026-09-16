package com.booking_hotel.payment_service.repository;
import com.booking_hotel.payment_service.entity.PaymentEntity;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.*;
public interface PaymentRepository extends JpaRepository<PaymentEntity,UUID> {
    Optional<PaymentEntity> findByBookingPublicId(UUID bookingPublicId);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from PaymentEntity p where p.paymentId=:id")
    Optional<PaymentEntity> findLockedById(@Param("id") UUID id);
}
