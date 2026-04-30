package com.wafrnalak.marketplace.enums;

/**
 * Order lifecycle states.
 * Persisted as a VARCHAR(20) string column (EnumType.STRING) in the {@code orders} table.
 * Adding or reordering constants is safe — the stored string value never changes.
 */
public enum OrderStatus {
    PENDING,
    PROCESSING,
    ACTIVE,
    COMPLETED,
    CANCELLED
}
