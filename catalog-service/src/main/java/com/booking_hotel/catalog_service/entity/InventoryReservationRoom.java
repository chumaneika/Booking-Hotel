package com.booking_hotel.catalog_service.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;
import java.time.LocalDate;

@Entity @Table(name="inventory_reservation_rooms",indexes=@Index(name="inventory_dates_idx",columnList="roomTypeId,checkInDate,checkOutDate"))
@Getter @Setter @NoArgsConstructor
public class InventoryReservationRoom {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false) private UUID reservationId;
    @Column(nullable=false) private Long roomTypeId;
    @Column(nullable=false) private Integer quantity;
    @Column(nullable=false) private LocalDate checkInDate;
    @Column(nullable=false) private LocalDate checkOutDate;
}
