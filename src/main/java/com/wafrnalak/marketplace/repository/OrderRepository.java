package com.wafrnalak.marketplace.repository;

import com.wafrnalak.marketplace.entity.Order;
import com.wafrnalak.marketplace.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    List<Order> findByCustomerCustomerId(Integer customerId);

    List<Order> findByCustomerCustomerIdAndStatus(Integer customerId, OrderStatus status);

    List<Order> findByCustomerCustomerIdOrderByOrderDateDesc(Integer customerId);
}
