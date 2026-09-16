package com.booking_hotel.payment_service.messaging;
import org.springframework.data.jpa.repository.JpaRepository;
public interface OutboxRepository extends JpaRepository<OutboxMessage,Long> {}

