package com.wafrnalak.marketplace.controller;

import com.wafrnalak.marketplace.auth.CustomerAuthContext;
import com.wafrnalak.marketplace.dto.request.CartCheckoutRequest;
import com.wafrnalak.marketplace.dto.request.CartItemUpsertRequest;
import com.wafrnalak.marketplace.dto.response.ApiResponse;
import com.wafrnalak.marketplace.dto.response.CartCheckoutResponse;
import com.wafrnalak.marketplace.dto.response.CartSummaryResponse;
import com.wafrnalak.marketplace.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final CustomerAuthContext customerAuthContext;

    @GetMapping
    public ResponseEntity<ApiResponse<CartSummaryResponse>> getMyCart() {
        Integer customerId = customerAuthContext.getCurrentCustomerId();
        return ResponseEntity.ok(ApiResponse.success(cartService.getCart(customerId)));
    }

    @PutMapping("/items")
    public ResponseEntity<ApiResponse<CartSummaryResponse>> upsertCartItem(
            @RequestBody @Valid CartItemUpsertRequest request) {
        Integer customerId = customerAuthContext.getCurrentCustomerId();
        CartSummaryResponse response = cartService.upsertItem(customerId, request);
        return ResponseEntity.ok(ApiResponse.success("Cart updated", response));
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<ApiResponse<CartSummaryResponse>> removeCartItem(
            @PathVariable Integer productId) {
        Integer customerId = customerAuthContext.getCurrentCustomerId();
        CartSummaryResponse response = cartService.removeItem(customerId, productId);
        return ResponseEntity.ok(ApiResponse.success("Cart item removed", response));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> clearCart() {
        Integer customerId = customerAuthContext.getCurrentCustomerId();
        cartService.clearCart(customerId);
        return ResponseEntity.ok(ApiResponse.success("Cart cleared", null));
    }

    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse<CartCheckoutResponse>> checkout(
            @RequestBody(required = false) @Valid CartCheckoutRequest request) {
        Integer customerId = customerAuthContext.getCurrentCustomerId();
        CartCheckoutResponse response = cartService.checkout(customerId, request);
        return ResponseEntity.ok(ApiResponse.success("Checkout completed", response));
    }
}
