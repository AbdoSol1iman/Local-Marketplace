package com.wafrnalak.marketplace.enums;

/**
 * Stored as smallint (ordinal) in the database:
 * 0 = PENDING, 1 = PROCESSING, 2 = ACTIVE, 3 = COMPLETED, 4 = CANCELLED
 */
public enum OrderStatus {
    PENDING,
    PROCESSING,
    ACTIVE,
    COMPLETED,
    CANCELLED
}
