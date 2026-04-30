package com.wafrnalak.marketplace.repository;

import com.wafrnalak.marketplace.entity.ProductCatalog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductCatalogRepository extends JpaRepository<ProductCatalog, Integer> {

    // ── Non-paginated (used internally for ownership checks, etc.) ────────────
    List<ProductCatalog> findByCategoryCategoryId(Integer categoryId);

    List<ProductCatalog> findByProductNameContainingIgnoreCase(String name);

    List<ProductCatalog> findByCategoryCategoryIdAndProductNameContainingIgnoreCase(
            Integer categoryId, String name);

    // ── Paginated search queries (used by searchProducts) ────────────────────
    Page<ProductCatalog> findByProductNameContainingIgnoreCase(String name, Pageable pageable);

    Page<ProductCatalog> findByCategoryCategoryId(Integer categoryId, Pageable pageable);

    Page<ProductCatalog> findByCategoryCategoryIdAndProductNameContainingIgnoreCase(
            Integer categoryId, String name, Pageable pageable);

    // ── Aggregate ─────────────────────────────────────────────────────────────
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.productId = :productId")
    Optional<Double> findAverageRatingByProductId(@Param("productId") Integer productId);
}
