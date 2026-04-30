package com.wafrnalak.marketplace.service;

import com.wafrnalak.marketplace.dto.request.OrderItemRequest;
import com.wafrnalak.marketplace.dto.request.PlaceOrderRequest;
import com.wafrnalak.marketplace.dto.response.OrderItemResponse;
import com.wafrnalak.marketplace.dto.response.OrderResponse;
import com.wafrnalak.marketplace.entity.Customer;
import com.wafrnalak.marketplace.entity.Order;
import com.wafrnalak.marketplace.entity.OrderItem;
import com.wafrnalak.marketplace.entity.OrderItemId;
import com.wafrnalak.marketplace.entity.ProductCatalog;
import com.wafrnalak.marketplace.enums.OrderStatus;
import com.wafrnalak.marketplace.exception.BusinessException;
import com.wafrnalak.marketplace.exception.ResourceNotFoundException;
import com.wafrnalak.marketplace.repository.CustomerRepository;
import com.wafrnalak.marketplace.repository.OrderItemRepository;
import com.wafrnalak.marketplace.repository.OrderRepository;
import com.wafrnalak.marketplace.repository.ProductCatalogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CustomerRepository customerRepository;
    private final ProductCatalogRepository productCatalogRepository;

    // -------------------------------------------------------------------------
    // Place Order
    // -------------------------------------------------------------------------

    public OrderResponse placeOrder(Integer customerId, PlaceOrderRequest request) {

        // 1. Load customer
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));

        // 2. Validate items list is not empty
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new BusinessException("Order must contain at least one item");
        }

        // 3. Validate every product exists and has sufficient stock (first pass)
        List<ProductCatalog> products = new ArrayList<>();
        for (OrderItemRequest itemReq : request.getItems()) {
            ProductCatalog product = productCatalogRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", itemReq.getProductId()));
            if (product.getQuantityInStock() < itemReq.getQuantity()) {
                throw new BusinessException("Insufficient stock for product: " + product.getProductName());
            }
            products.add(product);
        }

        // 4. Create the Order shell (totalAmount starts at ZERO)
        Order order = Order.builder()
                .customer(customer)
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.PENDING)
                .totalAmount(BigDecimal.ZERO)
                .build();

        // 5. Persist order first so we have a generated orderId
        Order savedOrder = orderRepository.save(order);

        // 6. Build order items, decrement stock, and save each product
        List<OrderItem> orderItems = new ArrayList<>();
        List<OrderItemRequest> itemRequests = request.getItems();

        for (int i = 0; i < itemRequests.size(); i++) {
            OrderItemRequest itemReq = itemRequests.get(i);
            ProductCatalog product = products.get(i);

            OrderItemId itemId = new OrderItemId(savedOrder.getOrderId(), product.getProductId());
            BigDecimal unitPrice = product.getPrice();
            BigDecimal totalItemsPrice = unitPrice.multiply(BigDecimal.valueOf(itemReq.getQuantity()));

            OrderItem orderItem = OrderItem.builder()
                    .id(itemId)
                    .order(savedOrder)
                    .product(product)
                    .quantity(itemReq.getQuantity())
                    .price(unitPrice)
                    .totalItemsPrice(totalItemsPrice)
                    .build();

            // Decrement stock
            product.setQuantityInStock(product.getQuantityInStock() - itemReq.getQuantity());
            productCatalogRepository.save(product);

            orderItems.add(orderItem);
        }

        // 7. Persist all order items atomically
        List<OrderItem> savedItems = orderItemRepository.saveAll(orderItems);

        // 8. Sum up the grand total
        BigDecimal total = savedItems.stream()
                .map(OrderItem::getTotalItemsPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 9. Update order with correct total and item list
        savedOrder.setTotalAmount(total);
        savedOrder.setItems(savedItems);

        // 10. Persist updated order and return response
        Order finalOrder = orderRepository.save(savedOrder);
        return toResponse(finalOrder, savedItems);
    }

    // -------------------------------------------------------------------------
    // Read Operations
    // -------------------------------------------------------------------------

    public OrderResponse getOrderById(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
        List<OrderItem> items = orderItemRepository.findByOrderOrderId(orderId);
        return toResponse(order, items);
    }

    public List<OrderResponse> getOrdersByCustomer(Integer customerId) {
        List<Order> orders = orderRepository.findByCustomerCustomerIdOrderByOrderDateDesc(customerId);
        return orders.stream()
                .map(order -> {
                    List<OrderItem> items = orderItemRepository.findByOrderOrderId(order.getOrderId());
                    return toResponse(order, items);
                })
                .collect(Collectors.toList());
    }

    public List<OrderResponse> getOrdersByCustomerAndStatus(Integer customerId, String statusStr) {
        OrderStatus status;
        try {
            status = OrderStatus.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Invalid order status: " + statusStr);
        }

        List<Order> orders = orderRepository.findByCustomerCustomerIdAndStatus(customerId, status);
        return orders.stream()
                .map(order -> {
                    List<OrderItem> items = orderItemRepository.findByOrderOrderId(order.getOrderId());
                    return toResponse(order, items);
                })
                .collect(Collectors.toList());
    }

    // -------------------------------------------------------------------------
    // Cancel Order
    // -------------------------------------------------------------------------

    public OrderResponse cancelOrder(Integer orderId, Integer customerId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        // Ownership check
        if (!order.getCustomer().getCustomerId().equals(customerId)) {
            throw new BusinessException("You can only cancel your own orders");
        }

        // Only PENDING or PROCESSING orders may be cancelled
        if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.PROCESSING) {
            throw new BusinessException("Order cannot be cancelled in its current state");
        }

        // Restore stock for every ordered item
        List<OrderItem> items = orderItemRepository.findByOrderOrderId(orderId);
        for (OrderItem item : items) {
            ProductCatalog product = productCatalogRepository.findById(item.getId().getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", item.getId().getProductId()));
            product.setQuantityInStock(product.getQuantityInStock() + item.getQuantity());
            productCatalogRepository.save(product);
        }

        order.setStatus(OrderStatus.CANCELLED);
        Order saved = orderRepository.save(order);
        return toResponse(saved, items);
    }

    // -------------------------------------------------------------------------
    // Update Order Status (admin / internal use)
    // -------------------------------------------------------------------------

    public OrderResponse updateOrderStatus(Integer orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        order.setStatus(newStatus);
        Order saved = orderRepository.save(order);
        List<OrderItem> items = orderItemRepository.findByOrderOrderId(orderId);
        return toResponse(saved, items);
    }

    // -------------------------------------------------------------------------
    // Mapping helper
    // -------------------------------------------------------------------------

    private OrderResponse toResponse(Order order, List<OrderItem> items) {
        List<OrderItemResponse> itemResponses = items.stream()
                .map(item -> OrderItemResponse.builder()
                        .orderId(order.getOrderId())
                        .productId(item.getId().getProductId())
                        .productName(item.getProduct().getProductName())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .totalItemsPrice(item.getTotalItemsPrice())
                        .build())
                .collect(Collectors.toList());

        return OrderResponse.builder()
                .orderId(order.getOrderId())
                .customerId(order.getCustomer().getCustomerId())
                .customerName(order.getCustomer().getName())
                .orderDate(order.getOrderDate())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus().name())
                .items(itemResponses)
                .build();
    }
}
