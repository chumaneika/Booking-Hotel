package com.booking_hotel.booking_service.messaging;
import org.springframework.data.jpa.repository.JpaRepository;
public interface OutboxRepository extends JpaRepository<OutboxMessage,Long> {}

