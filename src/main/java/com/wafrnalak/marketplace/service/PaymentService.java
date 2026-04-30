package com.wafrnalak.marketplace.service;

import com.wafrnalak.marketplace.dto.request.PaymentRequest;
import com.wafrnalak.marketplace.dto.response.PaymentResponse;
import com.wafrnalak.marketplace.entity.Order;
import com.wafrnalak.marketplace.entity.Payment;
import com.wafrnalak.marketplace.enums.OrderStatus;
import com.wafrnalak.marketplace.exception.BusinessException;
import com.wafrnalak.marketplace.exception.ResourceNotFoundException;
import com.wafrnalak.marketplace.repository.OrderRepository;
import com.wafrnalak.marketplace.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    // -------------------------------------------------------------------------
    // Create Payment
    // -------------------------------------------------------------------------

    public PaymentResponse createPayment(PaymentRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", request.getOrderId()));

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new BusinessException("Cannot process payment for a cancelled order");
        }

        Payment payment = Payment.builder()
                .order(order)
                .amount(request.getAmount())
                .paymentMethod(request.getPaymentMethod())
                .transactionDate(LocalDateTime.now())
                .build();

        Payment saved = paymentRepository.save(payment);
        return toResponse(saved);
    }

    // -------------------------------------------------------------------------
    // Read Operations
    // -------------------------------------------------------------------------

    public PaymentResponse getPaymentByOrder(Integer orderId) {
        Payment payment = paymentRepository.findFirstByOrderOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "orderId", orderId));
        return toResponse(payment);
    }

    public PaymentResponse getPaymentById(Integer paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", paymentId));
        return toResponse(payment);
    }

    // -------------------------------------------------------------------------
    // Mapping helper
    // -------------------------------------------------------------------------

    private PaymentResponse toResponse(Payment payment) {
        return PaymentResponse.builder()
                .paymentId(payment.getPaymentId())
                .orderId(payment.getOrder().getOrderId())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .transactionDate(payment.getTransactionDate())
                .build();
    }
}
