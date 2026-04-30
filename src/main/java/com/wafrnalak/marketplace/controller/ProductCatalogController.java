package com.wafrnalak.marketplace.controller;

import com.wafrnalak.marketplace.dto.request.ProductImageRequest;
import com.wafrnalak.marketplace.dto.request.ProductRequest;
import com.wafrnalak.marketplace.dto.response.ApiResponse;
import com.wafrnalak.marketplace.service.ProductCatalogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductCatalogController {

    private final ProductCatalogService productCatalogService;

    // -------------------------------------------------------------------------
    // POST /api/products  →  201 Created
    // -------------------------------------------------------------------------

    @PostMapping
    public ResponseEntity<ApiResponse<?>> createProduct(
            @RequestBody @Valid ProductRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Product created",
                        productCatalogService.createProduct(request)));
    }

    // -------------------------------------------------------------------------
    // GET /api/products?page=0&size=10  →  200 OK
    // -------------------------------------------------------------------------

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                ApiResponse.success(productCatalogService.getAllProducts(page, size)));
    }

    // -------------------------------------------------------------------------
    // GET /api/products/{id}  →  200 OK
    // -------------------------------------------------------------------------

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getProductById(
            @PathVariable Integer id) {

        return ResponseEntity.ok(
                ApiResponse.success(productCatalogService.getProductById(id)));
    }

    // -------------------------------------------------------------------------
    // GET /api/products/category/{categoryId}  →  200 OK
    // -------------------------------------------------------------------------

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<?>> getProductsByCategory(
            @PathVariable Integer categoryId) {

        return ResponseEntity.ok(
                ApiResponse.success(productCatalogService.getProductsByCategory(categoryId)));
    }

    // -------------------------------------------------------------------------
    // GET /api/products/search?name=&categoryId=&page=0&size=10  →  200 OK
    // -------------------------------------------------------------------------

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<?>> searchProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        productCatalogService.searchProducts(name, categoryId, page, size)));
    }

    // -------------------------------------------------------------------------
    // PUT /api/products/{id}  →  200 OK
    // -------------------------------------------------------------------------

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> updateProduct(
            @PathVariable Integer id,
            @RequestBody @Valid ProductRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success("Product updated",
                        productCatalogService.updateProduct(id, request)));
    }

    // -------------------------------------------------------------------------
    // DELETE /api/products/{id}  →  204 No Content
    // -------------------------------------------------------------------------

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> deleteProduct(
            @PathVariable Integer id) {

        productCatalogService.deleteProduct(id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.success("Product deleted", null));
    }

    // -------------------------------------------------------------------------
    // POST /api/products/{id}/images  →  201 Created
    // -------------------------------------------------------------------------

    @PostMapping("/{id}/images")
    public ResponseEntity<ApiResponse<?>> addImage(
            @PathVariable Integer id,
            @RequestBody @Valid ProductImageRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Image added",
                        productCatalogService.addImage(id, request)));
    }

    // -------------------------------------------------------------------------
    // DELETE /api/products/{productId}/images/{imageId}  →  204 No Content
    // -------------------------------------------------------------------------

    @DeleteMapping("/{productId}/images/{imageId}")
    public ResponseEntity<ApiResponse<?>> removeImage(
            @PathVariable Integer productId,
            @PathVariable Integer imageId) {

        productCatalogService.removeImage(productId, imageId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.success("Image removed", null));
    }
}
