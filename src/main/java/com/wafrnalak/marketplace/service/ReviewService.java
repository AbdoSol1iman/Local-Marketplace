package com.wafrnalak.marketplace.service;

import com.wafrnalak.marketplace.dto.request.ReviewRequest;
import com.wafrnalak.marketplace.dto.response.ReviewResponse;
import com.wafrnalak.marketplace.entity.Customer;
import com.wafrnalak.marketplace.entity.ProductCatalog;
import com.wafrnalak.marketplace.entity.Review;
import com.wafrnalak.marketplace.exception.BusinessException;
import com.wafrnalak.marketplace.exception.ResourceNotFoundException;
import com.wafrnalak.marketplace.repository.CustomerRepository;
import com.wafrnalak.marketplace.repository.ProductCatalogRepository;
import com.wafrnalak.marketplace.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductCatalogRepository productCatalogRepository;
    private final CustomerRepository customerRepository;

    // -------------------------------------------------------------------------
    // Create
    // -------------------------------------------------------------------------

    public ReviewResponse createReview(Integer customerId, ReviewRequest request) {

        // 1. Validate product exists
        ProductCatalog product = productCatalogRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", request.getProductId()));

        // 2. Validate customer exists
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));

        // 3. Prevent duplicate reviews
        if (reviewRepository.existsByProductProductIdAndCustomerCustomerId(
                request.getProductId(), customerId)) {
            throw new BusinessException("You have already reviewed this product");
        }

        // 4. Validate rating range [1, 5]
        if (request.getRating().compareTo(BigDecimal.ONE) < 0
                || request.getRating().compareTo(new BigDecimal("5")) > 0) {
            throw new BusinessException("Rating must be between 1 and 5");
        }

        // 5. Persist
        Review review = Review.builder()
                .product(product)
                .customer(customer)
                .reviewText(request.getReviewText())
                .rating(request.getRating())
                .reviewDate(LocalDateTime.now())
                .build();

        Review saved = reviewRepository.save(review);
        return toResponse(saved);
    }

    // -------------------------------------------------------------------------
    // Read – by product
    // -------------------------------------------------------------------------

    public List<ReviewResponse> getReviewsByProduct(Integer productId) {
        if (!productCatalogRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product", "id", productId);
        }

        return reviewRepository.findByProductProductId(productId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // -------------------------------------------------------------------------
    // Read – by customer
    // -------------------------------------------------------------------------

    public List<ReviewResponse> getReviewsByCustomer(Integer customerId) {
        return reviewRepository.findByCustomerCustomerId(customerId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // -------------------------------------------------------------------------
    // Delete
    // -------------------------------------------------------------------------

    public void deleteReview(Integer reviewId, Integer customerId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));

        if (!review.getCustomer().getCustomerId().equals(customerId)) {
            throw new BusinessException("You can only delete your own reviews");
        }

        reviewRepository.delete(review);
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private ReviewResponse toResponse(Review review) {
        return ReviewResponse.builder()
                .reviewId(review.getReviewId())
                .productId(review.getProduct().getProductId())
                .customerId(review.getCustomer().getCustomerId())
                .customerName(review.getCustomer().getName())
                .reviewText(review.getReviewText())
                .rating(review.getRating())
                .reviewDate(review.getReviewDate())
                .build();
    }
}
