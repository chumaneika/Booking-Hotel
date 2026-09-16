package com.booking_hotel.catalog_service.service;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component @RequiredArgsConstructor
public class InventoryCommandsListener {
    private final ObjectMapper mapper;
    private final InventoryService inventory;
    @KafkaListener(topics="${app.kafka.topics.inventory-reservation-commands}")
    public void consume(String json) throws Exception {
        var envelope=mapper.readTree(json); var payload=envelope.get("payload");
        if(payload==null || !payload.isObject()) throw new IllegalArgumentException("Missing command payload");
        switch(envelope.path("eventType").asText()) {
            case "ROOM_RESERVATION_REQUESTED" -> inventory.reserve(mapper.treeToValue(payload,InventoryService.Request.class));
            case "ROOM_RESERVATION_RELEASED" -> inventory.release(mapper.treeToValue(payload,InventoryService.Release.class));
            default -> throw new IllegalArgumentException("Unsupported inventory command");
        }
    }
}
