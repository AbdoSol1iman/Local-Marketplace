package com.wafrnalak.marketplace.auth;

import com.wafrnalak.marketplace.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

/**
 * Development-only implementation of {@link CustomerAuthContext}.
 *
 * Reads the customer ID from the {@code X-Customer-Id} HTTP request header.
 * This is a temporary stand-in until Firebase auth is integrated.
 *
 * When {@code security.insecure-test-mode=false} (production default), calling
 * {@link #getCurrentCustomerId()} throws an {@link UnsupportedOperationException}
 * to make it obvious that real auth must be wired before going to production.
 *
 * TODO: Replace this bean entirely with a Firebase token verifier once Firebase is integrated.
 */
@Component
@RequestScope
@RequiredArgsConstructor
public class DevCustomerAuthContext implements CustomerAuthContext {

    public static final String HEADER = "X-Customer-Id";

    @Value("${security.insecure-test-mode:false}")
    private boolean insecureTestMode;

    private final HttpServletRequest request;

    @Override
    public Integer getCurrentCustomerId() {
        if (!insecureTestMode) {
            throw new UnsupportedOperationException(
                "CustomerAuthContext is not yet backed by Firebase. " +
                "Enable security.insecure-test-mode=true (dev profile only) for local testing, " +
                "or implement Firebase token verification for production."
            );
        }

        String value = request.getHeader(HEADER);
        if (value == null || value.isBlank()) {
            throw new BusinessException(
                "Missing '" + HEADER + "' header. " +
                "In dev mode, pass the customer ID via this header.");
        }

        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new BusinessException(
                "Invalid '" + HEADER + "' header value: '" + value + "'. Must be an integer.");
        }
    }
}
