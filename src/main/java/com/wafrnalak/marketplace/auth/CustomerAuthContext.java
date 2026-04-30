package com.wafrnalak.marketplace.auth;

/**
 * Abstracts how the current customer's identity is resolved.
 *
 * In development (insecure-test-mode=true): implemented by {@link DevCustomerAuthContext},
 * which reads the customer ID from the {@code X-Customer-Id} request header.
 *
 * In production: replace {@link DevCustomerAuthContext} with a Firebase-backed
 * implementation that verifies the Bearer token and extracts the UID, then
 * looks up (or creates) the corresponding {@code Customer} record.
 */
public interface CustomerAuthContext {

    /**
     * Returns the ID of the currently authenticated customer.
     *
     * @throws com.wafrnalak.marketplace.exception.BusinessException
     *         if identity cannot be determined (missing/invalid token or header)
     */
    Integer getCurrentCustomerId();
}
