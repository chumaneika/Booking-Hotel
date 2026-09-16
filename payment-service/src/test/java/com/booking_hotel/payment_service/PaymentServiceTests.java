package com.booking_hotel.payment_service;

import com.booking_hotel.payment_service.entity.PaymentEntity;
import com.booking_hotel.payment_service.repository.PaymentRepository;
import com.booking_hotel.payment_service.messaging.Outbox;
import com.booking_hotel.payment_service.service.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class PaymentServiceTests {
    private final PaymentEntity payment=new PaymentEntity();
    private final List<String> events=new ArrayList<>();
    private final PaymentRepository repository=(PaymentRepository)Proxy.newProxyInstance(
            PaymentRepository.class.getClassLoader(),new Class<?>[]{PaymentRepository.class},(proxy,method,args)-> {
                return switch(method.getName()) {
                    case "findLockedById","findByBookingPublicId" -> Optional.of(payment);
                    case "save" -> args[0];
                    default -> throw new AssertionError("Unexpected repository call");
                };
            });
    private final Outbox outbox=new Outbox(null) {
        @Override public void enqueue(String topic,String key,String payload) { events.add(payload); }
    };
    private final PaymentService service=new PaymentService(repository,null,new ObjectMapper().findAndRegisterModules(),outbox);
    @BeforeEach void setup() {
        payment.setPaymentId(UUID.randomUUID()); payment.setBookingPublicId(UUID.randomUUID());
        payment.setUserId(1L); payment.setAmount(new BigDecimal("100.00"));
        payment.setCurrency("RUB"); payment.setStatus("PENDING");
        ReflectionTestUtils.setField(service,"eventsTopic","payment.events.v1");
        authenticate(1L);
    }
    @AfterEach void clearAuthentication() { SecurityContextHolder.clearContext(); }
    private void authenticate(Long id) {
        var authentication=new UsernamePasswordAuthenticationToken("user",null,List.of());
        authentication.setDetails(id); SecurityContextHolder.getContext().setAuthentication(authentication);
    }
    @Test void demoIsDisabledByDefault() {
        var error=assertThrows(ResponseStatusException.class,()->service.simulate(payment.getPaymentId(),PaymentService.DemoOutcome.SUCCEEDED));
        assertEquals(404,error.getStatusCode().value()); assertEquals("PENDING",payment.getStatus()); assertTrue(events.isEmpty());
    }
    @Test void onlyOwnerCanReadOrSimulateTheirPayment() {
        ReflectionTestUtils.setField(service,"demoEnabled",true); authenticate(2L);
        assertThrows(ResponseStatusException.class,()->service.getByBooking(payment.getBookingPublicId()));
        assertThrows(ResponseStatusException.class,()->service.simulate(payment.getPaymentId(),PaymentService.DemoOutcome.SUCCEEDED));
        assertTrue(events.isEmpty());
    }
    @Test void repeatedSimulationIsIdempotentAndConflictingOutcomeIsRejected() throws Exception {
        ReflectionTestUtils.setField(service,"demoEnabled",true);
        service.simulate(payment.getPaymentId(),PaymentService.DemoOutcome.SUCCEEDED);
        service.simulate(payment.getPaymentId(),PaymentService.DemoOutcome.SUCCEEDED);
        assertEquals(1,events.size());
        assertEquals("PAYMENT_SUCCEEDED",new ObjectMapper().readTree(events.get(0)).get("eventType").asText());
        assertTrue(payment.getProviderPaymentId().startsWith("demo-"));
        assertThrows(ResponseStatusException.class,()->service.simulate(payment.getPaymentId(),PaymentService.DemoOutcome.FAILED));
    }
}
