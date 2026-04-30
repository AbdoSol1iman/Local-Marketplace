package com.wafrnalak.marketplace.controller;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // -------------------------------------------------------------------------
    // POST /api/reviews?customerId=  →  201 Created
    // -------------------------------------------------------------------------

    @PostMapping
    public ResponseEntity<ApiResponse<?>> createReview(
            @RequestParam Integer customerId,
            @RequestBody @Valid ReviewRequest request) {

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
    // DELETE /api/reviews/{reviewId}?customerId=  →  204 No Content
    // -------------------------------------------------------------------------

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<?>> deleteReview(
            @PathVariable Integer reviewId,
            @RequestParam Integer customerId) {

        reviewService.deleteReview(reviewId, customerId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.success("Review deleted", null));
    }
}
