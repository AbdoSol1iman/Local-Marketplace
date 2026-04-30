package com.wafrnalak.marketplace.enums;

/**
 * Stored as smallint (ordinal) in the database:
 * 0 = PENDING, 1 = SHIPPED, 2 = IN_TRANSIT, 3 = DELIVERED, 4 = FAILED
 */
public enum ShippingStatus {
    PENDING,
    SHIPPED,
    IN_TRANSIT,
    DELIVERED,
    FAILED
}
