package com.wafrnalak.marketplace.controller;

import com.wafrnalak.marketplace.dto.request.ShippingUpdateRequest;
import com.wafrnalak.marketplace.dto.response.ApiResponse;
import com.wafrnalak.marketplace.dto.response.ShippingResponse;
import com.wafrnalak.marketplace.service.ShippingService;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/shipping")
@RequiredArgsConstructor
public class ShippingController {

    private final ShippingService shippingService;

    // -------------------------------------------------------------------------
    // POST /api/shipping/order/{orderId} — Create a shipping record for an order
    // -------------------------------------------------------------------------

    @PostMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<ShippingResponse>> createShipping(
            @PathVariable Integer orderId,
            @RequestBody @Valid ShippingUpdateRequest request) {

        ShippingResponse response = shippingService.createShipping(orderId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Shipping record created", response));
    }

    // -------------------------------------------------------------------------
    // PUT /api/shipping/order/{orderId} — Update an existing shipping record
    // -------------------------------------------------------------------------

    @PutMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<ShippingResponse>> updateShipping(
            @PathVariable Integer orderId,
            @RequestBody @Valid ShippingUpdateRequest request) {

        ShippingResponse response = shippingService.updateShipping(orderId, request);
        return ResponseEntity.ok(ApiResponse.success("Shipping updated", response));
    }

    // -------------------------------------------------------------------------
    // GET /api/shipping/order/{orderId} — Get shipping info for an order
    // -------------------------------------------------------------------------

    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<ShippingResponse>> getShippingByOrder(
            @PathVariable Integer orderId) {

        return ResponseEntity.ok(ApiResponse.success(shippingService.getShippingByOrder(orderId)));
    }
}
