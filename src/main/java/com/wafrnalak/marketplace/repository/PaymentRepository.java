package com.wafrnalak.marketplace.repository;

import com.wafrnalak.marketplace.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    List<Payment> findByOrderOrderId(Integer orderId);

    Optional<Payment> findFirstByOrderOrderId(Integer orderId);
}
