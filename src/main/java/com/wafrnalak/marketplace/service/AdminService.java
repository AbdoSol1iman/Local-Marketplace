package com.wafrnalak.marketplace.service;

import com.wafrnalak.marketplace.dto.request.StoreRequest;
import com.wafrnalak.marketplace.dto.response.AdminUserResponse;
import com.wafrnalak.marketplace.dto.response.StoreResponse;
import com.wafrnalak.marketplace.entity.Customer;
import com.wafrnalak.marketplace.entity.Store;
import com.wafrnalak.marketplace.exception.ResourceNotFoundException;
import com.wafrnalak.marketplace.repository.CustomerRepository;
import com.wafrnalak.marketplace.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminService {

    private final StoreRepository storeRepository;
    private final CustomerRepository customerRepository;

    @Transactional(readOnly = true)
    public List<StoreResponse> getStores() {
        return storeRepository.findAll().stream()
                .map(this::toStoreResponse)
                .collect(Collectors.toList());
    }

    public StoreResponse createStore(StoreRequest request) {
        Store store = Store.builder()
                .storeName(request.getStoreName().trim())
                .ownerName(request.getOwnerName().trim())
                .active(true)
                .build();
        Store saved = storeRepository.save(store);
        return toStoreResponse(saved);
    }

    public StoreResponse setStoreStatus(Integer storeId, boolean active) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Store", "id", storeId));
        store.setActive(active);
        Store saved = storeRepository.save(store);
        return toStoreResponse(saved);
    }

    public void deleteStore(Integer storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Store", "id", storeId));
        storeRepository.delete(store);
    }

    @Transactional(readOnly = true)
    public List<AdminUserResponse> getUsers() {
        return customerRepository.findAll().stream()
                .map(this::toAdminUserResponse)
                .collect(Collectors.toList());
    }

    public AdminUserResponse setUserBlocked(Integer customerId, boolean blocked) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));
        customer.setBlocked(blocked);
        Customer saved = customerRepository.save(customer);
        return toAdminUserResponse(saved);
    }

    public void deleteUser(Integer customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));
        customerRepository.delete(customer);
    }

    private StoreResponse toStoreResponse(Store store) {
        return StoreResponse.builder()
                .storeId(store.getStoreId())
                .storeName(store.getStoreName())
                .ownerName(store.getOwnerName())
                .active(Boolean.TRUE.equals(store.getActive()))
                .build();
    }

    private AdminUserResponse toAdminUserResponse(Customer customer) {
        return AdminUserResponse.builder()
                .customerId(customer.getCustomerId())
                .name(customer.getName())
                .email(customer.getEmail())
                .blocked(Boolean.TRUE.equals(customer.getBlocked()))
                .build();
    }
}
