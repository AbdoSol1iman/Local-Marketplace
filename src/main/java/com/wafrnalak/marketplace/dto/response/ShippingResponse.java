package com.wafrnalak.marketplace.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShippingResponse {

    private Integer shippingId;

    private Integer orderId;

    private String carrierName;

    private String trackingNumber;

    private String shippingStatus;

    private LocalDateTime estimatedDeliveryDate;

    private LocalDateTime actualDeliveryDate;
}
