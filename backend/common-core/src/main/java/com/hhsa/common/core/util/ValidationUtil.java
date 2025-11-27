package com.hhsa.common.core.util;

import java.math.BigDecimal;
import java.util.regex.Pattern;

/**
 * Utility class for validation operations.
 */
public class ValidationUtil {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    private static final Pattern ALPHANUMERIC_PATTERN = Pattern.compile("^[A-Za-z0-9]+$");

    /**
     * Validate email format
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Validate alphanumeric string
     */
    public static boolean isAlphanumeric(String value) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }
        return ALPHANUMERIC_PATTERN.matcher(value).matches();
    }

    /**
     * Validate string length
     */
    public static boolean isValidLength(String value, int minLength, int maxLength) {
        if (value == null) {
            return minLength == 0;
        }
        int length = value.length();
        return length >= minLength && length <= maxLength;
    }

    /**
     * Validate that BigDecimal is positive
     */
    public static boolean isPositive(BigDecimal value) {
        return value != null && value.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Validate that BigDecimal is non-negative
     */
    public static boolean isNonNegative(BigDecimal value) {
        return value != null && value.compareTo(BigDecimal.ZERO) >= 0;
    }

    /**
     * Validate that BigDecimal is within range
     */
    public static boolean isInRange(BigDecimal value, BigDecimal min, BigDecimal max) {
        if (value == null || min == null || max == null) {
            return false;
        }
        return value.compareTo(min) >= 0 && value.compareTo(max) <= 0;
    }

    /**
     * Check if string is not blank (not null, not empty, not whitespace)
     */
    public static boolean isNotBlank(String value) {
        return value != null && !value.trim().isEmpty();
    }

    /**
     * Check if string is blank (null, empty, or whitespace)
     */
    public static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    /**
     * Validate that two BigDecimal values are equal (with scale consideration)
     */
    public static boolean areEqual(BigDecimal value1, BigDecimal value2) {
        if (value1 == null && value2 == null) {
            return true;
        }
        if (value1 == null || value2 == null) {
            return false;
        }
        return value1.compareTo(value2) == 0;
    }
}




