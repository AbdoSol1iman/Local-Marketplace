package com.wafrnalak.marketplace.repository;

import com.wafrnalak.marketplace.entity.CartItem;
import com.wafrnalak.marketplace.entity.CartItemId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, CartItemId> {

    List<CartItem> findByCustomerCustomerId(Integer customerId);

    Optional<CartItem> findByCustomerCustomerIdAndProductProductId(Integer customerId, Integer productId);

    void deleteByCustomerCustomerId(Integer customerId);
}
