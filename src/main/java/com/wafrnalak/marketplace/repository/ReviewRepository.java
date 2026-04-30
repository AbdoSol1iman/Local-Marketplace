package com.wafrnalak.marketplace.repository;

import com.wafrnalak.marketplace.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {

    List<Review> findByProductProductId(Integer productId);

    List<Review> findByCustomerCustomerId(Integer customerId);

    boolean existsByProductProductIdAndCustomerCustomerId(Integer productId, Integer customerId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.productId = :productId")
    Optional<Double> findAverageRatingByProductId(@Param("productId") Integer productId);
}
