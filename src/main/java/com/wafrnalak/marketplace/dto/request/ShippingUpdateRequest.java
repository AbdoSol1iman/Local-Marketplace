package com.wafrnalak.marketplace.dto.request;

import com.wafrnalak.marketplace.enums.ShippingStatus;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShippingUpdateRequest {

    @Size(max = 100)
    private String carrierName;

    @Size(max = 50)
    private String trackingNumber;

    private ShippingStatus shippingStatus;

    private LocalDateTime estimatedDeliveryDate;

    private LocalDateTime actualDeliveryDate;
}
