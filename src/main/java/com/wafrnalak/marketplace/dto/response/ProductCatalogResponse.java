package com.wafrnalak.marketplace.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductCatalogResponse {

    private Integer productId;

    private String productName;

    private String description;

    private BigDecimal price;

    private Integer quantityInStock;

    private ProductCategoryResponse category;

    private List<ProductImageResponse> images;

    private Double averageRating;
}
