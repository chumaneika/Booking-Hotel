package com.booking_hotel.payment_service.service;

import com.booking_hotel.payment_service.entity.PaymentEntity;
import com.booking_hotel.payment_service.repository.PaymentRepository;
import com.booking_hotel.payment_service.messaging.Outbox;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

@Service @RequiredArgsConstructor
public class PaymentService {
    public record Request(UUID paymentId,UUID bookingPublicId,Long userId,BigDecimal amount,String currency) {}
    public enum DemoOutcome { SUCCEEDED, FAILED }
    private final PaymentRepository payments;
    private final JdbcTemplate jdbc;
    private final ObjectMapper mapper;
    private final Outbox outbox;
    @Value("${app.kafka.topics.payment-events}") private String eventsTopic;
    @Value("${app.payment.demo-enabled:false}") private boolean demoEnabled;

    @Transactional
    public void request(Request r) {
        if(r.paymentId()==null || r.bookingPublicId()==null || r.userId()==null || r.amount()==null
                || r.amount().signum()<=0 || r.currency()==null || !r.currency().matches("[A-Z]{3}"))
            throw new IllegalArgumentException("Invalid payment command");
        jdbc.queryForList("select pg_advisory_xact_lock(hashtextextended(?,0))",r.paymentId().toString());
        var existing=payments.findById(r.paymentId());
        if(existing.isPresent()) {
            var p=existing.get();
            if(!p.getBookingPublicId().equals(r.bookingPublicId()) || !p.getUserId().equals(r.userId())
                    || p.getAmount().compareTo(r.amount())!=0 || !p.getCurrency().equals(r.currency()))
                throw new IllegalArgumentException("Payment identity mismatch");
            if(!p.getStatus().equals("PENDING")) publish(p);
            return;
        }
        var p=new PaymentEntity(); p.setPaymentId(r.paymentId()); p.setBookingPublicId(r.bookingPublicId());
        p.setUserId(r.userId()); p.setAmount(r.amount()); p.setCurrency(r.currency());
        p.setStatus("PENDING"); p.setCreatedAt(Instant.now()); payments.save(p);
    }
    @Transactional(readOnly=true)
    public PaymentEntity getByBooking(UUID booking) {
        var p=payments.findByBookingPublicId(booking).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Payment not found"));
        ensureOwner(p); return p;
    }
    @Transactional
    public PaymentEntity simulate(UUID id,DemoOutcome outcome) {
        if(!demoEnabled) throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Demo payments disabled");
        var p=payments.findLockedById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Payment not found"));
        ensureOwner(p);
        if(outcome==null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Missing outcome");
        if(!p.getStatus().equals("PENDING")) {
            if(!p.getStatus().equals(outcome.name())) throw new ResponseStatusException(HttpStatus.CONFLICT,"Payment already completed");
            return p;
        }
        p.setStatus(outcome.name());
        if(outcome==DemoOutcome.SUCCEEDED) p.setProviderPaymentId("demo-"+p.getPaymentId());
        else p.setReason("Demo payment declined");
        payments.save(p); publish(p); return p;
    }
    private void ensureOwner(PaymentEntity p) {
        var auth=SecurityContextHolder.getContext().getAuthentication();
        boolean admin=auth!=null && auth.getAuthorities().stream().anyMatch(a->a.getAuthority().equals("ROLE_ADMIN"));
        if(!admin && (auth==null || !p.getUserId().equals(auth.getDetails())))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Access denied");
    }
    private void publish(PaymentEntity p) {
        var body=new LinkedHashMap<String,Object>(); body.put("eventId",UUID.randomUUID()); body.put("occurredAt",Instant.now());
        body.put("paymentId",p.getPaymentId()); body.put("bookingPublicId",p.getBookingPublicId());
        boolean success=p.getStatus().equals("SUCCEEDED");
        if(success) body.put("providerPaymentId",p.getProviderPaymentId()); else body.put("reason",p.getReason());
        try { outbox.enqueue(eventsTopic,p.getBookingPublicId().toString(),mapper.writeValueAsString(
                Map.of("eventType",success?"PAYMENT_SUCCEEDED":"PAYMENT_FAILED","payload",body))); }
        catch(Exception e) { throw new IllegalStateException("Cannot serialize payment result",e); }
    }
}
