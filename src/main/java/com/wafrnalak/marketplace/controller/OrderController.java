package com.wafrnalak.marketplace.controller;

import com.wafrnalak.marketplace.dto.request.PlaceOrderRequest;
import com.wafrnalak.marketplace.dto.response.ApiResponse;
import com.wafrnalak.marketplace.dto.response.OrderResponse;
import com.wafrnalak.marketplace.enums.OrderStatus;
import com.wafrnalak.marketplace.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // -------------------------------------------------------------------------
    // POST /api/orders — Place a new order
    // -------------------------------------------------------------------------

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> placeOrder(
            @RequestParam Integer customerId,
            @RequestBody @Valid PlaceOrderRequest request) {

        OrderResponse response = orderService.placeOrder(customerId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Order placed successfully", response));
    }

    // -------------------------------------------------------------------------
    // GET /api/orders/{orderId} — Fetch a single order by ID
    // -------------------------------------------------------------------------

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(
            @PathVariable Integer orderId) {

        return ResponseEntity.ok(ApiResponse.success(orderService.getOrderById(orderId)));
    }

    // -------------------------------------------------------------------------
    // GET /api/orders/customer/{customerId} — All orders for a customer
    // -------------------------------------------------------------------------

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getOrdersByCustomer(
            @PathVariable Integer customerId) {

        return ResponseEntity.ok(ApiResponse.success(orderService.getOrdersByCustomer(customerId)));
    }

    // -------------------------------------------------------------------------
    // GET /api/orders/customer/{customerId}/status — Orders filtered by status
    // -------------------------------------------------------------------------

    @GetMapping("/customer/{customerId}/status")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getOrdersByCustomerAndStatus(
            @PathVariable Integer customerId,
            @RequestParam String status) {

        return ResponseEntity.ok(
                ApiResponse.success(orderService.getOrdersByCustomerAndStatus(customerId, status)));
    }

    // -------------------------------------------------------------------------
    // PUT /api/orders/{orderId}/cancel — Cancel an order (customer-initiated)
    // -------------------------------------------------------------------------

    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(
            @PathVariable Integer orderId,
            @RequestParam Integer customerId) {

        OrderResponse response = orderService.cancelOrder(orderId, customerId);
        return ResponseEntity.ok(ApiResponse.success("Order cancelled", response));
    }

    // -------------------------------------------------------------------------
    // PUT /api/orders/{orderId}/status — Update order status (admin/internal)
    // -------------------------------------------------------------------------

    @PutMapping("/{orderId}/status")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @PathVariable Integer orderId,
            @RequestParam String status) {

        OrderStatus parsedStatus;
        try {
            parsedStatus = OrderStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error("Invalid status: " + status));
        }

        OrderResponse response = orderService.updateOrderStatus(orderId, parsedStatus);
        return ResponseEntity.ok(ApiResponse.success("Order status updated", response));
    }
}
