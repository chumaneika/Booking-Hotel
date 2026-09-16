package com.booking_hotel.payment_service.controller;
import com.booking_hotel.payment_service.entity.PaymentEntity;
import com.booking_hotel.payment_service.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController @RequestMapping("/api/payments") @RequiredArgsConstructor
public class PaymentController {
    public record DemoRequest(PaymentService.DemoOutcome outcome) {}
    private final PaymentService payments;
    @GetMapping public PaymentEntity get(@RequestParam UUID bookingPublicId) { return payments.getByBooking(bookingPublicId); }
    @PostMapping("/{paymentId}/demo")
    public PaymentEntity simulate(@PathVariable UUID paymentId,@RequestBody DemoRequest request) {
        return payments.simulate(paymentId,request.outcome());
    }
}
