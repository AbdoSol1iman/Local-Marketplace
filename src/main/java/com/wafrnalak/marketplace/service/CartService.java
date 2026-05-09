package com.wafrnalak.marketplace.service;

import com.wafrnalak.marketplace.dto.request.CartCheckoutRequest;
import com.wafrnalak.marketplace.dto.request.CartItemUpsertRequest;
import com.wafrnalak.marketplace.dto.request.OrderItemRequest;
import com.wafrnalak.marketplace.dto.request.PaymentRequest;
import com.wafrnalak.marketplace.dto.request.PlaceOrderRequest;
import com.wafrnalak.marketplace.dto.response.CartCheckoutResponse;
import com.wafrnalak.marketplace.dto.response.CartItemResponse;
import com.wafrnalak.marketplace.dto.response.CartSummaryResponse;
import com.wafrnalak.marketplace.dto.response.OrderResponse;
import com.wafrnalak.marketplace.dto.response.PaymentResponse;
import com.wafrnalak.marketplace.dto.response.ShippingResponse;
import com.wafrnalak.marketplace.entity.CartItem;
import com.wafrnalak.marketplace.entity.CartItemId;
import com.wafrnalak.marketplace.entity.Customer;
import com.wafrnalak.marketplace.entity.ProductCatalog;
import com.wafrnalak.marketplace.entity.ProductImages;
import com.wafrnalak.marketplace.exception.BusinessException;
import com.wafrnalak.marketplace.exception.ResourceNotFoundException;
import com.wafrnalak.marketplace.repository.CartItemRepository;
import com.wafrnalak.marketplace.repository.CustomerRepository;
import com.wafrnalak.marketplace.repository.ProductCatalogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private static final BigDecimal TAX_RATE = new BigDecimal("0.08");
    private static final BigDecimal SHIPPING_FEE = new BigDecimal("40.00");
    private static final BigDecimal FREE_SHIPPING_THRESHOLD = new BigDecimal("500.00");

    private final CartItemRepository cartItemRepository;
    private final CustomerRepository customerRepository;
    private final ProductCatalogRepository productCatalogRepository;
    private final OrderService orderService;
    private final PaymentService paymentService;
    private final ShippingService shippingService;

    @Transactional(readOnly = true)
    public CartSummaryResponse getCart(Integer customerId) {
        assertCustomerExists(customerId);
        List<CartItem> items = cartItemRepository.findByCustomerCustomerId(customerId);
        return toSummary(customerId, items);
    }

    public CartSummaryResponse upsertItem(Integer customerId, CartItemUpsertRequest request) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));
        ProductCatalog product = productCatalogRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", request.getProductId()));

        validateStock(product, request.getQuantity());

        CartItem cartItem = cartItemRepository
                .findByCustomerCustomerIdAndProductProductId(customerId, request.getProductId())
                .orElseGet(() -> CartItem.builder()
                        .id(new CartItemId(customerId, request.getProductId()))
                        .customer(customer)
                        .product(product)
                        .build());

        cartItem.setQuantity(request.getQuantity());
        cartItemRepository.save(cartItem);
        return getCart(customerId);
    }

    public CartSummaryResponse removeItem(Integer customerId, Integer productId) {
        CartItem item = cartItemRepository.findByCustomerCustomerIdAndProductProductId(customerId, productId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item", "productId", productId));
        cartItemRepository.delete(item);
        return getCart(customerId);
    }

    public void clearCart(Integer customerId) {
        assertCustomerExists(customerId);
        cartItemRepository.deleteByCustomerCustomerId(customerId);
    }

    public CartCheckoutResponse checkout(Integer customerId, CartCheckoutRequest request) {
        List<CartItem> cartItems = cartItemRepository.findByCustomerCustomerId(customerId);
        if (cartItems.isEmpty()) {
            throw new BusinessException("Cannot checkout an empty cart");
        }

        PlaceOrderRequest placeOrderRequest = PlaceOrderRequest.builder()
                .items(cartItems.stream()
                        .map(item -> OrderItemRequest.builder()
                                .productId(item.getProduct().getProductId())
                                .quantity(item.getQuantity())
                                .build())
                        .collect(Collectors.toList()))
                .build();

        OrderResponse order = orderService.placeOrder(customerId, placeOrderRequest);

        PaymentResponse payment = null;
        if (request != null && request.getPaymentMethod() != null && !request.getPaymentMethod().isBlank()) {
            PaymentRequest paymentRequest = PaymentRequest.builder()
                    .orderId(order.getOrderId())
                    .amount(order.getTotalAmount())
                    .paymentMethod(request.getPaymentMethod().trim())
                    .build();
            payment = paymentService.createPayment(paymentRequest);
        }

        ShippingResponse shipping = shippingService.createPendingShippingForOrder(order.getOrderId());
        cartItemRepository.deleteByCustomerCustomerId(customerId);

        return CartCheckoutResponse.builder()
                .order(order)
                .payment(payment)
                .shipping(shipping)
                .build();
    }

    private void validateStock(ProductCatalog product, Integer requestedQuantity) {
        if (product.getQuantityInStock() == null || product.getQuantityInStock() <= 0) {
            throw new BusinessException("Product is out of stock: " + product.getProductName());
        }
        if (requestedQuantity > product.getQuantityInStock()) {
            throw new BusinessException("Requested quantity exceeds stock for: " + product.getProductName());
        }
    }

    private void assertCustomerExists(Integer customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer", "id", customerId);
        }
    }

    private CartSummaryResponse toSummary(Integer customerId, List<CartItem> items) {
        List<CartItemResponse> itemResponses = items.stream()
                .map(this::toItemResponse)
                .collect(Collectors.toList());

        BigDecimal subtotal = itemResponses.stream()
                .map(CartItemResponse::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal tax = subtotal.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal shippingFee = subtotal.signum() == 0
                ? BigDecimal.ZERO
                : (subtotal.compareTo(FREE_SHIPPING_THRESHOLD) >= 0 ? BigDecimal.ZERO : SHIPPING_FEE);
        BigDecimal total = subtotal.add(tax).add(shippingFee).setScale(2, RoundingMode.HALF_UP);

        int totalQuantity = itemResponses.stream()
                .map(CartItemResponse::getQuantity)
                .reduce(0, Integer::sum);

        return CartSummaryResponse.builder()
                .customerId(customerId)
                .itemCount(itemResponses.size())
                .totalQuantity(totalQuantity)
                .subtotal(subtotal.setScale(2, RoundingMode.HALF_UP))
                .tax(tax)
                .shippingFee(shippingFee.setScale(2, RoundingMode.HALF_UP))
                .total(total)
                .items(itemResponses)
                .build();
    }

    private CartItemResponse toItemResponse(CartItem item) {
        BigDecimal unitPrice = item.getProduct().getPrice();
        BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(item.getQuantity()));

        return CartItemResponse.builder()
                .productId(item.getProduct().getProductId())
                .productName(item.getProduct().getProductName())
                .imageUrl(resolvePrimaryImage(item.getProduct()))
                .unitPrice(unitPrice)
                .quantity(item.getQuantity())
                .lineTotal(lineTotal.setScale(2, RoundingMode.HALF_UP))
                .build();
    }

    private String resolvePrimaryImage(ProductCatalog product) {
        if (product.getImages() == null || product.getImages().isEmpty()) {
            return null;
        }
        return product.getImages().stream()
                .sorted(Comparator.comparing(
                        ProductImages::getImageOrder,
                        Comparator.nullsLast(Comparator.naturalOrder())
                ))
                .map(ProductImages::getImageUrl)
                .findFirst()
                .orElse(null);
    }
}
