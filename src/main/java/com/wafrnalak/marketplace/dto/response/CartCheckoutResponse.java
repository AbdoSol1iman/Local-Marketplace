package com.wafrnalak.marketplace.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartCheckoutResponse {

    private OrderResponse order;

    private PaymentResponse payment;

    private ShippingResponse shipping;
}
