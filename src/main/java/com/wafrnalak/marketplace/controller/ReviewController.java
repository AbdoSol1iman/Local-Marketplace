package com.wafrnalak.marketplace.controller;

import com.wafrnalak.marketplace.auth.CustomerAuthContext;
import com.wafrnalak.marketplace.dto.request.ReviewRequest;
import com.wafrnalak.marketplace.dto.response.ApiResponse;
import com.wafrnalak.marketplace.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final CustomerAuthContext customerAuthContext;

    // -------------------------------------------------------------------------
    // POST /api/reviews  →  201 Created
    // Customer identity is derived from auth context (X-Customer-Id header in dev,
    // Firebase token in prod).
    // -------------------------------------------------------------------------

    @PostMapping
    public ResponseEntity<ApiResponse<?>> createReview(
            @RequestBody @Valid ReviewRequest request) {

        Integer customerId = customerAuthContext.getCurrentCustomerId();
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Review submitted",
                        reviewService.createReview(customerId, request)));
    }

    // -------------------------------------------------------------------------
    // GET /api/reviews/product/{productId}  →  200 OK
    // -------------------------------------------------------------------------

    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<?>> getReviewsByProduct(
            @PathVariable Integer productId) {

        return ResponseEntity.ok(
                ApiResponse.success(reviewService.getReviewsByProduct(productId)));
    }

    // -------------------------------------------------------------------------
    // GET /api/reviews/customer/{customerId}  →  200 OK
    // -------------------------------------------------------------------------

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ApiResponse<?>> getReviewsByCustomer(
            @PathVariable Integer customerId) {

        return ResponseEntity.ok(
                ApiResponse.success(reviewService.getReviewsByCustomer(customerId)));
    }

    // -------------------------------------------------------------------------
    // DELETE /api/reviews/{reviewId}  →  204 No Content
    // Customer identity is derived from auth context, not from the request.
    // -------------------------------------------------------------------------

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Integer reviewId) {

        Integer customerId = customerAuthContext.getCurrentCustomerId();
        reviewService.deleteReview(reviewId, customerId);
        return ResponseEntity.noContent().build();
    }
}
