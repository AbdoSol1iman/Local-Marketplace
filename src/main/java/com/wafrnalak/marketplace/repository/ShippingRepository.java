package com.wafrnalak.marketplace.repository;

import com.wafrnalak.marketplace.entity.Shipping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShippingRepository extends JpaRepository<Shipping, Integer> {

    Optional<Shipping> findByOrderOrderId(Integer orderId);
}
