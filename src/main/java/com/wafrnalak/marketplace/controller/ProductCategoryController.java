package com.wafrnalak.marketplace.controller;

import com.wafrnalak.marketplace.dto.request.ProductCategoryRequest;
import com.wafrnalak.marketplace.dto.response.ApiResponse;
import com.wafrnalak.marketplace.dto.response.ProductCategoryResponse;
import com.wafrnalak.marketplace.service.ProductCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class ProductCategoryController {

    private final ProductCategoryService categoryService;

    // POST /api/categories
    @PostMapping
    public ResponseEntity<ApiResponse<ProductCategoryResponse>> create(
            @RequestBody @Valid ProductCategoryRequest request) {

        return ResponseEntity
                .status(201)
                .body(ApiResponse.success("Category created", categoryService.create(request)));
    }

    // GET /api/categories
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductCategoryResponse>>> getAll() {

        return ResponseEntity.ok(ApiResponse.success(categoryService.getAll()));
    }

    // GET /api/categories/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductCategoryResponse>> getById(
            @PathVariable Integer id) {

        return ResponseEntity.ok(ApiResponse.success(categoryService.getById(id)));
    }

    // PUT /api/categories/{id}
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductCategoryResponse>> update(
            @PathVariable Integer id,
            @RequestBody @Valid ProductCategoryRequest request) {

        return ResponseEntity.ok(ApiResponse.success("Category updated", categoryService.update(id, request)));
    }

    // DELETE /api/categories/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Integer id) {

        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
