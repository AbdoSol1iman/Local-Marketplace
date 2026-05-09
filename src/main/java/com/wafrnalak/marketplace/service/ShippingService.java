package com.wafrnalak.marketplace.service;

import com.wafrnalak.marketplace.dto.request.ShippingUpdateRequest;
import com.wafrnalak.marketplace.dto.response.ShippingResponse;
import com.wafrnalak.marketplace.entity.Order;
import com.wafrnalak.marketplace.entity.Shipping;
import com.wafrnalak.marketplace.enums.ShippingStatus;
import com.wafrnalak.marketplace.exception.BusinessException;
import com.wafrnalak.marketplace.exception.ResourceNotFoundException;
import com.wafrnalak.marketplace.repository.OrderRepository;
import com.wafrnalak.marketplace.repository.ShippingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ShippingService {

    private final ShippingRepository shippingRepository;
    private final OrderRepository orderRepository;

    // -------------------------------------------------------------------------
    // Create Shipping
    // -------------------------------------------------------------------------

    public ShippingResponse createShipping(Integer orderId, ShippingUpdateRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        // Guard: only one shipping record per order
        if (shippingRepository.findByOrderOrderId(orderId).isPresent()) {
            throw new BusinessException("Shipping record already exists for this order");
        }

        ShippingStatus status = request.getShippingStatus() != null
                ? request.getShippingStatus()
                : ShippingStatus.PENDING;

        Shipping shipping = Shipping.builder()
                .order(order)
                .carrierName(request.getCarrierName())
                .trackingNumber(request.getTrackingNumber())
                .shippingStatus(status)
                .estimatedDeliveryDate(request.getEstimatedDeliveryDate())
                .actualDeliveryDate(request.getActualDeliveryDate())
                .build();

        Shipping saved = shippingRepository.save(shipping);
        return toResponse(saved);
    }

    // -------------------------------------------------------------------------
    // Update Shipping (partial — only non-null fields are changed)
    // -------------------------------------------------------------------------

    public ShippingResponse updateShipping(Integer orderId, ShippingUpdateRequest request) {
        Shipping shipping = shippingRepository.findByOrderOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Shipping", "orderId", orderId));

        if (request.getCarrierName() != null) {
            shipping.setCarrierName(request.getCarrierName());
        }
        if (request.getTrackingNumber() != null) {
            shipping.setTrackingNumber(request.getTrackingNumber());
        }
        if (request.getShippingStatus() != null) {
            shipping.setShippingStatus(request.getShippingStatus());
        }
        if (request.getEstimatedDeliveryDate() != null) {
            shipping.setEstimatedDeliveryDate(request.getEstimatedDeliveryDate());
        }
        if (request.getActualDeliveryDate() != null) {
            shipping.setActualDeliveryDate(request.getActualDeliveryDate());
        }

        Shipping saved = shippingRepository.save(shipping);
        return toResponse(saved);
    }

    // -------------------------------------------------------------------------
    // Read Operation
    // -------------------------------------------------------------------------

    public ShippingResponse getShippingByOrder(Integer orderId) {
        Shipping shipping = shippingRepository.findByOrderOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Shipping", "orderId", orderId));
        return toResponse(shipping);
    }

    public ShippingResponse createPendingShippingForOrder(Integer orderId) {
        Shipping shipping = shippingRepository.findByOrderOrderId(orderId)
                .orElseGet(() -> {
                    Order order = orderRepository.findById(orderId)
                            .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
                    Shipping pending = Shipping.builder()
                            .order(order)
                            .shippingStatus(ShippingStatus.PENDING)
                            .estimatedDeliveryDate(LocalDateTime.now().plusDays(2))
                            .build();
                    return shippingRepository.save(pending);
                });
        return toResponse(shipping);
    }

    // -------------------------------------------------------------------------
    // Mapping helper
    // -------------------------------------------------------------------------

    private ShippingResponse toResponse(Shipping shipping) {
        return ShippingResponse.builder()
                .shippingId(shipping.getShippingId())
                .orderId(shipping.getOrder().getOrderId())
                .carrierName(shipping.getCarrierName())
                .trackingNumber(shipping.getTrackingNumber())
                .shippingStatus(shipping.getShippingStatus().name())
                .estimatedDeliveryDate(shipping.getEstimatedDeliveryDate())
                .actualDeliveryDate(shipping.getActualDeliveryDate())
                .build();
    }
}
