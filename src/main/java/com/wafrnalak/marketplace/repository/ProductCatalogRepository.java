package com.wafrnalak.marketplace.repository;

import com.wafrnalak.marketplace.entity.ProductCatalog;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
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

    // ── Pessimistic lock — use when modifying stock inside a transaction ────────
    /**
     * Fetches a product and immediately acquires a DB-level exclusive row lock
     * (SELECT … FOR UPDATE). Use this inside {@code @Transactional} write operations
     * that modify {@code quantityInStock} to prevent concurrent overselling.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM ProductCatalog p WHERE p.productId = :id")
    Optional<ProductCatalog> findByIdWithLock(@Param("id") Integer id);

    // ── Aggregate ─────────────────────────────────────────────────────────────
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.productId = :productId")
    Optional<Double> findAverageRatingByProductId(@Param("productId") Integer productId);
}
