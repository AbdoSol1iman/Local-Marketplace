package com.wafrnalak.marketplace.repository;

import com.wafrnalak.marketplace.entity.ProductCatalog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductCatalogRepository extends JpaRepository<ProductCatalog, Integer> {

    List<ProductCatalog> findByCategoryCategoryId(Integer categoryId);

    List<ProductCatalog> findByProductNameContainingIgnoreCase(String name);

    List<ProductCatalog> findByCategoryCategoryIdAndProductNameContainingIgnoreCase(
            Integer categoryId, String name);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.productId = :productId")
    Optional<Double> findAverageRatingByProductId(@Param("productId") Integer productId);
}
