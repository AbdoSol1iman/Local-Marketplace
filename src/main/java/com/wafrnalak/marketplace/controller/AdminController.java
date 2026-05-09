package com.wafrnalak.marketplace.controller;

import com.wafrnalak.marketplace.dto.request.StoreRequest;
import com.wafrnalak.marketplace.dto.response.AdminUserResponse;
import com.wafrnalak.marketplace.dto.response.ApiResponse;
import com.wafrnalak.marketplace.dto.response.StoreResponse;
import com.wafrnalak.marketplace.service.AdminService;
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

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/stores")
    public ResponseEntity<ApiResponse<List<StoreResponse>>> getStores() {
        return ResponseEntity.ok(ApiResponse.success(adminService.getStores()));
    }

    @PostMapping("/stores")
    public ResponseEntity<ApiResponse<StoreResponse>> createStore(
            @RequestBody @Valid StoreRequest request) {
        StoreResponse response = adminService.createStore(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Store created", response));
    }

    @PutMapping("/stores/{storeId}/status")
    public ResponseEntity<ApiResponse<StoreResponse>> updateStoreStatus(
            @PathVariable Integer storeId,
            @RequestParam boolean active) {
        StoreResponse response = adminService.setStoreStatus(storeId, active);
        return ResponseEntity.ok(ApiResponse.success("Store status updated", response));
    }

    @DeleteMapping("/stores/{storeId}")
    public ResponseEntity<Void> deleteStore(@PathVariable Integer storeId) {
        adminService.deleteStore(storeId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<AdminUserResponse>>> getUsers() {
        return ResponseEntity.ok(ApiResponse.success(adminService.getUsers()));
    }

    @PutMapping("/users/{customerId}/block")
    public ResponseEntity<ApiResponse<AdminUserResponse>> setUserBlocked(
            @PathVariable Integer customerId,
            @RequestParam boolean blocked) {
        AdminUserResponse response = adminService.setUserBlocked(customerId, blocked);
        return ResponseEntity.ok(ApiResponse.success("User status updated", response));
    }

    @DeleteMapping("/users/{customerId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer customerId) {
        adminService.deleteUser(customerId);
        return ResponseEntity.noContent().build();
    }
}
