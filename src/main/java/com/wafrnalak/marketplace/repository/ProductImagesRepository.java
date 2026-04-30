package com.wafrnalak.marketplace.repository;

import com.wafrnalak.marketplace.entity.ProductImages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ProductImagesRepository extends JpaRepository<ProductImages, Integer> {

    List<ProductImages> findByProductProductId(Integer productId);

    @Transactional
    void deleteByProductProductId(Integer productId);
}
