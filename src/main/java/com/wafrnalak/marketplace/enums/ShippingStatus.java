package com.wafrnalak.marketplace.enums;

/**
 * Shipping lifecycle states.
 * Persisted as a VARCHAR(20) string column (EnumType.STRING) in the {@code shippings} table.
 * Adding or reordering constants is safe — the stored string value never changes.
 */
public enum ShippingStatus {
    PENDING,
    SHIPPED,
    IN_TRANSIT,
    DELIVERED,
    FAILED
}
