package com.booking_hotel.payment_service.service;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component @RequiredArgsConstructor
public class PaymentCommandsListener {
    private final ObjectMapper mapper;
    private final PaymentService payments;
    @KafkaListener(topics="${app.kafka.topics.payment-commands}")
    public void consume(String json) throws Exception {
        var envelope=mapper.readTree(json);
        if(!envelope.path("eventType").asText().equals("PAYMENT_REQUESTED") || !envelope.path("payload").isObject())
            throw new IllegalArgumentException("Unsupported payment command");
        payments.request(mapper.treeToValue(envelope.get("payload"),PaymentService.Request.class));
    }
}
