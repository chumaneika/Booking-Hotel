package com.booking_hotel.catalog_service.messaging;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.*;
@Component @RequiredArgsConstructor
public class Outbox {
    private final OutboxRepository repository;
    @Transactional(propagation=Propagation.MANDATORY)
    public void enqueue(String topic,String key,String payload) {
        repository.save(new OutboxMessage(topic,key,payload));
    }
}

