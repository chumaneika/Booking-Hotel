package com.booking_hotel.payment_service.messaging;
import org.springframework.context.annotation.*;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;
@Configuration
public class KafkaErrors {
    @Bean public DefaultErrorHandler kafkaErrorHandler() {
        var handler=new DefaultErrorHandler(new FixedBackOff(1000L,Long.MAX_VALUE));
        handler.addNotRetryableExceptions(IllegalArgumentException.class, com.fasterxml.jackson.core.JsonProcessingException.class);
        return handler;
    }
}
