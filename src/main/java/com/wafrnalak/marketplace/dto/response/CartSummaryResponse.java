package com.wafrnalak.marketplace.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartSummaryResponse {

    private Integer customerId;

    private Integer itemCount;

    private Integer totalQuantity;

    private BigDecimal subtotal;

    private BigDecimal tax;

    private BigDecimal shippingFee;

    private BigDecimal total;

    private List<CartItemResponse> items;
}
