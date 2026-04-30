package com.wafrnalak.marketplace.service;

import com.wafrnalak.marketplace.dto.request.ProductImageRequest;
import com.wafrnalak.marketplace.dto.request.ProductRequest;
import com.wafrnalak.marketplace.dto.response.PageResponse;
import com.wafrnalak.marketplace.dto.response.ProductCatalogResponse;
import com.wafrnalak.marketplace.dto.response.ProductCategoryResponse;
import com.wafrnalak.marketplace.dto.response.ProductImageResponse;
import com.wafrnalak.marketplace.entity.ProductCatalog;
import com.wafrnalak.marketplace.entity.ProductCategory;
import com.wafrnalak.marketplace.entity.ProductImages;
import com.wafrnalak.marketplace.exception.BusinessException;
import com.wafrnalak.marketplace.exception.ResourceNotFoundException;
import com.wafrnalak.marketplace.repository.ProductCatalogRepository;
import com.wafrnalak.marketplace.repository.ProductCategoryRepository;
import com.wafrnalak.marketplace.repository.ProductImagesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductCatalogService {

    private final ProductCatalogRepository productCatalogRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final ProductImagesRepository productImagesRepository;

    // -------------------------------------------------------------------------
    // Create
    // -------------------------------------------------------------------------

    public ProductCatalogResponse createProduct(ProductRequest request) {
        ProductCategory category = productCategoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));

        ProductCatalog product = ProductCatalog.builder()
                .productName(request.getProductName())
                .description(request.getDescription())
                .price(request.getPrice())
                .quantityInStock(request.getQuantityInStock())
                .category(category)
                .build();

        ProductCatalog saved = productCatalogRepository.save(product);
        return toResponse(saved);
    }

    // -------------------------------------------------------------------------
    // Read – paginated list
    // -------------------------------------------------------------------------

    @Transactional(readOnly = true)
    public PageResponse<ProductCatalogResponse> getAllProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductCatalog> result = productCatalogRepository.findAll(pageable);

        List<ProductCatalogResponse> content = result.getContent().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return PageResponse.<ProductCatalogResponse>builder()
                .content(content)
                .page(result.getNumber())
                .size(result.getSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .last(result.isLast())
                .build();
    }

    // -------------------------------------------------------------------------
    // Read – single product
    // -------------------------------------------------------------------------

    @Transactional(readOnly = true)
    public ProductCatalogResponse getProductById(Integer productId) {
        ProductCatalog product = productCatalogRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        return toResponse(product);
    }

    // -------------------------------------------------------------------------
    // Read – by category
    // -------------------------------------------------------------------------

    @Transactional(readOnly = true)
    public List<ProductCatalogResponse> getProductsByCategory(Integer categoryId) {
        return productCatalogRepository.findByCategoryCategoryId(categoryId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // -------------------------------------------------------------------------
    // Search (name + optional category, with pagination fallback)
    // -------------------------------------------------------------------------

    @Transactional(readOnly = true)
    public PageResponse<ProductCatalogResponse> searchProducts(
            String name, Integer categoryId, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ProductCatalog> result;

        if (name != null && categoryId != null) {
            result = productCatalogRepository
                    .findByCategoryCategoryIdAndProductNameContainingIgnoreCase(categoryId, name, pageable);
        } else if (name != null) {
            result = productCatalogRepository
                    .findByProductNameContainingIgnoreCase(name, pageable);
        } else if (categoryId != null) {
            result = productCatalogRepository
                    .findByCategoryCategoryId(categoryId, pageable);
        } else {
            result = productCatalogRepository.findAll(pageable);
        }

        List<ProductCatalogResponse> content = result.getContent().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return PageResponse.<ProductCatalogResponse>builder()
                .content(content)
                .page(result.getNumber())
                .size(result.getSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .last(result.isLast())
                .build();
    }

    // -------------------------------------------------------------------------
    // Update
    // -------------------------------------------------------------------------

    public ProductCatalogResponse updateProduct(Integer productId, ProductRequest request) {
        ProductCatalog product = productCatalogRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        ProductCategory category = productCategoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));

        product.setProductName(request.getProductName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setQuantityInStock(request.getQuantityInStock());
        product.setCategory(category);

        ProductCatalog saved = productCatalogRepository.save(product);
        return toResponse(saved);
    }

    // -------------------------------------------------------------------------
    // Delete
    // -------------------------------------------------------------------------

    public void deleteProduct(Integer productId) {
        ProductCatalog product = productCatalogRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        productCatalogRepository.delete(product);
    }

    // -------------------------------------------------------------------------
    // Image management
    // -------------------------------------------------------------------------

    public ProductCatalogResponse addImage(Integer productId, ProductImageRequest request) {
        ProductCatalog product = productCatalogRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        ProductImages image = ProductImages.builder()
                .imageUrl(request.getImageUrl())
                .imageOrder(request.getImageOrder())
                .product(product)
                .build();

        productImagesRepository.save(image);

        // Re-fetch to pick up the freshly persisted image in the collection
        return toResponse(productCatalogRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId)));
    }

    public void removeImage(Integer productId, Integer imageId) {
        productCatalogRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        ProductImages image = productImagesRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Image", "id", imageId));

        if (!image.getProduct().getProductId().equals(productId)) {
            throw new BusinessException("Image does not belong to this product");
        }

        productImagesRepository.delete(image);
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private ProductCatalogResponse toResponse(ProductCatalog p) {
        Double averageRating = productCatalogRepository
                .findAverageRatingByProductId(p.getProductId())
                .orElse(null);

        List<ProductImageResponse> images = p.getImages() == null
                ? Collections.emptyList()
                : p.getImages().stream()
                        .map(this::toImageResponse)
                        .collect(Collectors.toList());

        return ProductCatalogResponse.builder()
                .productId(p.getProductId())
                .productName(p.getProductName())
                .description(p.getDescription())
                .price(p.getPrice())
                .quantityInStock(p.getQuantityInStock())
                .category(toCategoryResponse(p.getCategory()))
                .images(images)
                .averageRating(averageRating)
                .build();
    }

    private ProductCategoryResponse toCategoryResponse(ProductCategory c) {
        return ProductCategoryResponse.builder()
                .categoryId(c.getCategoryId())
                .categoryName(c.getCategoryName())
                .build();
    }

    private ProductImageResponse toImageResponse(ProductImages img) {
        return ProductImageResponse.builder()
                .imageId(img.getImageId())
                .imageUrl(img.getImageUrl())
                .imageOrder(img.getImageOrder())
                .build();
    }


}
