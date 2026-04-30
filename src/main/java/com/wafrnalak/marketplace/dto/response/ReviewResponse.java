package com.wafrnalak.marketplace.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewResponse {

    private Integer reviewId;

    private Integer productId;

    private Integer customerId;

    private String customerName;

    private String reviewText;

    private BigDecimal rating;

    private LocalDateTime reviewDate;
}
