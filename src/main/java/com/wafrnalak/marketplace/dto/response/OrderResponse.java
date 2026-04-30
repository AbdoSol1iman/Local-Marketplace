package com.wafrnalak.marketplace.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {

    private Integer orderId;

    private Integer customerId;

    private String customerName;

    private LocalDateTime orderDate;

    private BigDecimal totalAmount;

    private String status;

    private List<OrderItemResponse> items;
}
