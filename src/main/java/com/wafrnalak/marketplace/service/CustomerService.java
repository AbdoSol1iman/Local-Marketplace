package com.wafrnalak.marketplace.service;

import com.wafrnalak.marketplace.dto.request.RegisterRequest;
import com.wafrnalak.marketplace.dto.request.UpdateCustomerRequest;
import com.wafrnalak.marketplace.dto.request.UpdatePasswordRequest;
import com.wafrnalak.marketplace.dto.response.CustomerResponse;
import com.wafrnalak.marketplace.entity.Customer;
import com.wafrnalak.marketplace.exception.BusinessException;
import com.wafrnalak.marketplace.exception.ResourceNotFoundException;
import com.wafrnalak.marketplace.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomerResponse register(RegisterRequest request) {
        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already registered");
        }
        if (customerRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("Username already taken");
        }

        Customer customer = Customer.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(request.getAddress())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .blocked(false)
                .build();

        Customer saved = customerRepository.save(customer);
        return toResponse(saved);
    }

    public CustomerResponse getById(Integer customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));
        return toResponse(customer);
    }

    public CustomerResponse getByUsername(String username) {
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "username", username));
        return toResponse(customer);
    }

    public CustomerResponse update(Integer customerId, UpdateCustomerRequest request) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));

        if (request.getName() != null) {
            customer.setName(request.getName());
        }
        if (request.getPhone() != null) {
            customer.setPhone(request.getPhone());
        }
        if (request.getAddress() != null) {
            customer.setAddress(request.getAddress());
        }

        Customer saved = customerRepository.save(customer);
        return toResponse(saved);
    }

    public void updatePassword(Integer customerId, UpdatePasswordRequest request) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));

        if (!passwordEncoder.matches(request.getCurrentPassword(), customer.getPassword())) {
            throw new BusinessException("Current password is incorrect");
        }
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException("Passwords do not match");
        }

        customer.setPassword(passwordEncoder.encode(request.getNewPassword()));
        customerRepository.save(customer);
    }

    public void delete(Integer customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));
        customerRepository.delete(customer);
    }

    public List<CustomerResponse> getAll() {
        return customerRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public CustomerResponse setBlocked(Integer customerId, boolean blocked) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));
        customer.setBlocked(blocked);
        Customer saved = customerRepository.save(customer);
        return toResponse(saved);
    }

    private CustomerResponse toResponse(Customer customer) {
        return CustomerResponse.builder()
                .customerId(customer.getCustomerId())
                .name(customer.getName())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .address(customer.getAddress())
                .username(customer.getUsername())
                .blocked(Boolean.TRUE.equals(customer.getBlocked()))
                .build();
    }
}
