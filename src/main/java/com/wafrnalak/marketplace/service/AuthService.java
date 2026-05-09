package com.wafrnalak.marketplace.service;

import com.wafrnalak.marketplace.dto.request.AuthRegisterRequest;
import com.wafrnalak.marketplace.dto.request.ForgotPasswordRequest;
import com.wafrnalak.marketplace.dto.request.LoginRequest;
import com.wafrnalak.marketplace.dto.response.AuthResponse;
import com.wafrnalak.marketplace.entity.Customer;
import com.wafrnalak.marketplace.exception.BusinessException;
import com.wafrnalak.marketplace.exception.ResourceNotFoundException;
import com.wafrnalak.marketplace.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponse register(AuthRegisterRequest request) {
        String email = normalizeEmail(request.getEmail());
        if (customerRepository.existsByEmail(email)) {
            throw new BusinessException("Email already registered");
        }

        String username = resolveUsername(request);
        if (customerRepository.existsByUsername(username)) {
            throw new BusinessException("Username already taken");
        }

        Customer customer = Customer.builder()
                .name(request.getName().trim())
                .email(email)
                .phone(request.getPhone())
                .address(request.getAddress())
                .username(username)
                .password(passwordEncoder.encode(request.getPassword()))
                .blocked(false)
                .build();

        Customer saved = customerRepository.save(customer);
        String token = jwtService.generateToken(saved);
        return toAuthResponse(saved, token);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String identifier = request.getUsername().trim();

        Customer customer = identifier.contains("@")
                ? customerRepository.findByEmail(normalizeEmail(identifier))
                        .orElseThrow(() -> new BusinessException("Invalid credentials"))
                : customerRepository.findByUsername(identifier)
                        .or(() -> customerRepository.findByEmail(normalizeEmail(identifier)))
                        .orElseThrow(() -> new BusinessException("Invalid credentials"));

        if (Boolean.TRUE.equals(customer.getBlocked())) {
            throw new BusinessException("Your account is blocked. Please contact support.");
        }

        if (!passwordEncoder.matches(request.getPassword(), customer.getPassword())) {
            throw new BusinessException("Invalid credentials");
        }

        String token = jwtService.generateToken(customer);
        return toAuthResponse(customer, token);
    }

    @Transactional(readOnly = true)
    public void requestPasswordReset(ForgotPasswordRequest request) {
        String email = normalizeEmail(request.getEmail());
        if (!customerRepository.existsByEmail(email)) {
            throw new ResourceNotFoundException("Customer", "email", email);
        }
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private String resolveUsername(AuthRegisterRequest request) {
        if (request.getUsername() != null && !request.getUsername().isBlank()) {
            return request.getUsername().trim();
        }

        String emailPart = normalizeEmail(request.getEmail()).split("@")[0]
                .replaceAll("[^a-zA-Z0-9._-]", "");

        String base = emailPart.isBlank() ? "customer" : emailPart;
        String candidate = base;
        int suffix = 1;
        while (customerRepository.existsByUsername(candidate)) {
            candidate = base + suffix;
            suffix++;
        }
        return candidate;
    }

    private AuthResponse toAuthResponse(Customer customer, String token) {
        return AuthResponse.builder()
                .token(token)
                .customerId(customer.getCustomerId())
                .username(customer.getUsername())
                .email(customer.getEmail())
                .build();
    }
}
