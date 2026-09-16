package com.booking_hotel.booking_service.messaging;
import jakarta.persistence.*;
import lombok.*;
@Entity @Table(name="kafka_outbox")
@Getter @NoArgsConstructor
public class OutboxMessage {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false) private String topic;
    @Column(nullable=false) private String messageKey;
    @Column(nullable=false,columnDefinition="text") private String payload;
    public OutboxMessage(String topic,String messageKey,String payload) {
        this.topic=topic; this.messageKey=messageKey; this.payload=payload;
    }
}

