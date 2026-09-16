package com.booking_hotel.catalog_service.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity @Table(name="inventory_reservations")
@Getter @Setter @NoArgsConstructor
public class InventoryReservation {
    @Id private UUID reservationId;
    @Column(nullable=false) private UUID bookingPublicId;
    @Column(nullable=false) private String status;
    private String reason;
}
