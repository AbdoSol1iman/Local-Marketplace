package com.wafrnalak.marketplace.service;

import com.wafrnalak.marketplace.dto.request.ProductCategoryRequest;
import com.wafrnalak.marketplace.dto.response.ProductCategoryResponse;
import com.wafrnalak.marketplace.entity.ProductCategory;
import com.wafrnalak.marketplace.exception.BusinessException;
import com.wafrnalak.marketplace.exception.ResourceNotFoundException;
import com.wafrnalak.marketplace.repository.ProductCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductCategoryService {

    private final ProductCategoryRepository productCategoryRepository;

    public ProductCategoryResponse create(ProductCategoryRequest request) {
        productCategoryRepository.findByCategoryNameIgnoreCase(request.getCategoryName())
                .ifPresent(existing -> {
                    throw new BusinessException("Category already exists");
                });

        ProductCategory category = ProductCategory.builder()
                .categoryName(request.getCategoryName())
                .build();

        ProductCategory saved = productCategoryRepository.save(category);
        return toResponse(saved);
    }

    public List<ProductCategoryResponse> getAll() {
        return productCategoryRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ProductCategoryResponse getById(Integer categoryId) {
        ProductCategory category = productCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
        return toResponse(category);
    }

    public ProductCategoryResponse update(Integer categoryId, ProductCategoryRequest request) {
        ProductCategory category = productCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));

        category.setCategoryName(request.getCategoryName());

        ProductCategory saved = productCategoryRepository.save(category);
        return toResponse(saved);
    }

    public void delete(Integer categoryId) {
        ProductCategory category = productCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
        productCategoryRepository.delete(category);
    }

    private ProductCategoryResponse toResponse(ProductCategory category) {
        return ProductCategoryResponse.builder()
                .categoryId(category.getCategoryId())
                .categoryName(category.getCategoryName())
                .build();
    }
}
