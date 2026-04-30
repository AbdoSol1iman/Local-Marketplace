package com.wafrnalak.marketplace.repository;

import com.wafrnalak.marketplace.entity.OrderItem;
import com.wafrnalak.marketplace.entity.OrderItemId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, OrderItemId> {

    List<OrderItem> findByOrderOrderId(Integer orderId);
}
