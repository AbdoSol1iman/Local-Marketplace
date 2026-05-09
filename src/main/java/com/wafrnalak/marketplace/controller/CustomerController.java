package com.wafrnalak.marketplace.controller;

import com.wafrnalak.marketplace.auth.CustomerAuthContext;
import com.wafrnalak.marketplace.dto.request.RegisterRequest;
import com.wafrnalak.marketplace.dto.request.UpdateCustomerRequest;
import com.wafrnalak.marketplace.dto.request.UpdatePasswordRequest;
import com.wafrnalak.marketplace.dto.response.ApiResponse;
import com.wafrnalak.marketplace.dto.response.CustomerResponse;
import com.wafrnalak.marketplace.service.CustomerService;
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

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    private final CustomerAuthContext customerAuthContext;

    // POST /api/customers/register
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<CustomerResponse>> register(
            @RequestBody @Valid RegisterRequest request) {

        CustomerResponse result = customerService.register(request);
        return ResponseEntity
                .status(201)
                .body(ApiResponse.success("Customer registered successfully", result));
    }

    // GET /api/customers/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CustomerResponse>> getById(
            @PathVariable Integer id) {

        return ResponseEntity.ok(ApiResponse.success(customerService.getById(id)));
    }

    // GET /api/customers/me
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<CustomerResponse>> getCurrentCustomer() {
        Integer customerId = customerAuthContext.getCurrentCustomerId();
        return ResponseEntity.ok(ApiResponse.success(customerService.getById(customerId)));
    }

    // GET /api/customers/username/{username}
    @GetMapping("/username/{username}")
    public ResponseEntity<ApiResponse<CustomerResponse>> getByUsername(
            @PathVariable String username) {

        return ResponseEntity.ok(ApiResponse.success(customerService.getByUsername(username)));
    }

    // PUT /api/customers/{id}
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CustomerResponse>> update(
            @PathVariable Integer id,
            @RequestBody @Valid UpdateCustomerRequest request) {

        return ResponseEntity.ok(ApiResponse.success("Customer updated", customerService.update(id, request)));
    }

    // PUT /api/customers/me
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<CustomerResponse>> updateCurrentCustomer(
            @RequestBody @Valid UpdateCustomerRequest request) {
        Integer customerId = customerAuthContext.getCurrentCustomerId();
        return ResponseEntity.ok(ApiResponse.success("Customer updated", customerService.update(customerId, request)));
    }

    // PUT /api/customers/{id}/password
    @PutMapping("/{id}/password")
    public ResponseEntity<ApiResponse<Void>> updatePassword(
            @PathVariable Integer id,
            @RequestBody @Valid UpdatePasswordRequest request) {

        customerService.updatePassword(id, request);
        return ResponseEntity.ok(ApiResponse.success("Password updated successfully", null));
    }

    // PUT /api/customers/me/password
    @PutMapping("/me/password")
    public ResponseEntity<ApiResponse<Void>> updateCurrentCustomerPassword(
            @RequestBody @Valid UpdatePasswordRequest request) {
        Integer customerId = customerAuthContext.getCurrentCustomerId();
        customerService.updatePassword(customerId, request);
        return ResponseEntity.ok(ApiResponse.success("Password updated successfully", null));
    }

    // DELETE /api/customers/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Integer id) {

        customerService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // DELETE /api/customers/me
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteCurrentCustomer() {
        Integer customerId = customerAuthContext.getCurrentCustomerId();
        customerService.delete(customerId);
        return ResponseEntity.noContent().build();
    }
}
