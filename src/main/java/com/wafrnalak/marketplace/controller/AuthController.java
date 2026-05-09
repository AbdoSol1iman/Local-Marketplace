package com.wafrnalak.marketplace.controller;

import com.wafrnalak.marketplace.dto.request.AuthRegisterRequest;
import com.wafrnalak.marketplace.dto.request.ForgotPasswordRequest;
import com.wafrnalak.marketplace.dto.request.LoginRequest;
import com.wafrnalak.marketplace.dto.response.ApiResponse;
import com.wafrnalak.marketplace.dto.response.AuthResponse;
import com.wafrnalak.marketplace.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @RequestBody @Valid AuthRegisterRequest request) {

        AuthResponse response = authService.register(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Account created successfully", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @RequestBody @Valid LoginRequest request) {

        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(
            @RequestBody @Valid ForgotPasswordRequest request) {

        authService.requestPasswordReset(request);
        return ResponseEntity.ok(ApiResponse.success("Password reset request accepted", null));
    }
}
