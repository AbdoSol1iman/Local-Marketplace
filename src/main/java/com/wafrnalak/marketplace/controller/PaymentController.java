package com.wafrnalak.marketplace.controller;

import com.wafrnalak.marketplace.dto.request.PaymentRequest;
import com.wafrnalak.marketplace.dto.response.ApiResponse;
import com.wafrnalak.marketplace.dto.response.PaymentResponse;
import com.wafrnalak.marketplace.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // -------------------------------------------------------------------------
    // POST /api/payments — Process a payment for an order
    // -------------------------------------------------------------------------

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentResponse>> createPayment(
            @RequestBody @Valid PaymentRequest request) {

        PaymentResponse response = paymentService.createPayment(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Payment processed", response));
    }

    // -------------------------------------------------------------------------
    // GET /api/payments/{paymentId} — Fetch a payment by its own ID
    // -------------------------------------------------------------------------

    @GetMapping("/{paymentId}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentById(
            @PathVariable Integer paymentId) {

        return ResponseEntity.ok(ApiResponse.success(paymentService.getPaymentById(paymentId)));
    }

    // -------------------------------------------------------------------------
    // GET /api/payments/order/{orderId} — Fetch the payment linked to an order
    // -------------------------------------------------------------------------

    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentByOrder(
            @PathVariable Integer orderId) {

        return ResponseEntity.ok(ApiResponse.success(paymentService.getPaymentByOrder(orderId)));
    }
}
