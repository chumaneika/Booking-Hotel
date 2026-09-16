package com.booking_hotel.payment_service.entity;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;
import java.math.BigDecimal;
import java.time.Instant;

@Entity @Table(name="payments",uniqueConstraints=@UniqueConstraint(columnNames="booking_public_id"))
@Getter @Setter @NoArgsConstructor
public class PaymentEntity {
    @Id private UUID paymentId;
    @Column(name="booking_public_id",nullable=false) private UUID bookingPublicId;
    @Column(nullable=false) private Long userId;
    @Column(nullable=false,precision=19,scale=2) private BigDecimal amount;
    @Column(nullable=false) private String currency;
    @Column(nullable=false) private String status;
    @Column(nullable=false) private Instant createdAt;
    private String providerPaymentId;
    private String reason;
}
