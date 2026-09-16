package com.booking_hotel.catalog_service.repository;
import com.booking_hotel.catalog_service.entity.InventoryReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
public interface InventoryReservationRepository extends JpaRepository<InventoryReservation,UUID> {}
