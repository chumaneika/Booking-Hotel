package com.booking_hotel.catalog_service.messaging;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.transaction.support.TransactionTemplate;
import java.util.concurrent.TimeUnit;
@Configuration @EnableScheduling @RequiredArgsConstructor @Slf4j
public class OutboxDispatcher {
    private final JdbcTemplate jdbc;
    private final TransactionTemplate transactions;
    private final KafkaTemplate<Object, Object> kafka;
    @Scheduled(fixedDelayString="${app.outbox.delay-ms:500}")
    public void dispatch() {
        try {
            transactions.executeWithoutResult(status -> {
                if(!Boolean.TRUE.equals(jdbc.queryForObject("select pg_try_advisory_xact_lock(hashtextextended('kafka_outbox_dispatcher',0))",Boolean.class))) return;
                var rows=jdbc.queryForList("select id, topic, message_key, payload from kafka_outbox order by id limit 50 for update skip locked");
                for(var row:rows) {
                    try {
                        kafka.send((String)row.get("topic"),(String)row.get("message_key"),(String)row.get("payload"))
                            .get(10,TimeUnit.SECONDS);
                        jdbc.update("delete from kafka_outbox where id=?",row.get("id"));
                    } catch(InterruptedException e) {
                        Thread.currentThread().interrupt(); throw new IllegalStateException("Outbox delivery interrupted",e);
                    } catch(Exception e) { throw new IllegalStateException("Outbox delivery failed",e); }
                }
            });
        } catch(Exception e) { log.warn("Outbox delivery deferred: {}",e.getClass().getSimpleName()); }
    }
}
