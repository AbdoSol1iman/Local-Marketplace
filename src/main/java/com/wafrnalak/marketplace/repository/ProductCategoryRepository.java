package com.wafrnalak.marketplace.repository;

import com.wafrnalak.marketplace.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Integer> {

    Optional<ProductCategory> findByCategoryNameIgnoreCase(String categoryName);

    List<ProductCategory> findByCategoryNameContainingIgnoreCase(String name);
}
