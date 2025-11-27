package com.hhsa.common.core.util;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ValidationUtil
 */
class ValidationUtilTest {

    @Test
    void testIsValidEmail() {
        assertTrue(ValidationUtil.isValidEmail("test@example.com"));
        assertTrue(ValidationUtil.isValidEmail("user.name@example.co.uk"));
        assertFalse(ValidationUtil.isValidEmail("invalid-email"));
        assertFalse(ValidationUtil.isValidEmail(""));
        assertFalse(ValidationUtil.isValidEmail(null));
    }

    @Test
    void testIsAlphanumeric() {
        assertTrue(ValidationUtil.isAlphanumeric("abc123"));
        assertTrue(ValidationUtil.isAlphanumeric("ABC123"));
        assertFalse(ValidationUtil.isAlphanumeric("abc-123"));
        assertFalse(ValidationUtil.isAlphanumeric("abc 123"));
        assertFalse(ValidationUtil.isAlphanumeric(""));
        assertFalse(ValidationUtil.isAlphanumeric(null));
    }

    @Test
    void testIsValidLength() {
        assertTrue(ValidationUtil.isValidLength("abc", 1, 5));
        assertTrue(ValidationUtil.isValidLength("abc", 3, 3));
        assertFalse(ValidationUtil.isValidLength("abc", 4, 10));
        assertFalse(ValidationUtil.isValidLength("abcdef", 1, 5));
        assertTrue(ValidationUtil.isValidLength(null, 0, 10));
        assertFalse(ValidationUtil.isValidLength(null, 1, 10));
    }

    @Test
    void testIsPositive() {
        assertTrue(ValidationUtil.isPositive(new BigDecimal("10.5")));
        assertTrue(ValidationUtil.isPositive(new BigDecimal("0.01")));
        assertFalse(ValidationUtil.isPositive(new BigDecimal("0")));
        assertFalse(ValidationUtil.isPositive(new BigDecimal("-1")));
        assertFalse(ValidationUtil.isPositive(null));
    }

    @Test
    void testIsNonNegative() {
        assertTrue(ValidationUtil.isNonNegative(new BigDecimal("10.5")));
        assertTrue(ValidationUtil.isNonNegative(new BigDecimal("0")));
        assertFalse(ValidationUtil.isNonNegative(new BigDecimal("-1")));
        assertFalse(ValidationUtil.isNonNegative(null));
    }

    @Test
    void testIsInRange() {
        BigDecimal min = new BigDecimal("10");
        BigDecimal max = new BigDecimal("100");
        
        assertTrue(ValidationUtil.isInRange(new BigDecimal("50"), min, max));
        assertTrue(ValidationUtil.isInRange(new BigDecimal("10"), min, max));
        assertTrue(ValidationUtil.isInRange(new BigDecimal("100"), min, max));
        assertFalse(ValidationUtil.isInRange(new BigDecimal("9"), min, max));
        assertFalse(ValidationUtil.isInRange(new BigDecimal("101"), min, max));
        assertFalse(ValidationUtil.isInRange(null, min, max));
    }

    @Test
    void testIsNotBlank() {
        assertTrue(ValidationUtil.isNotBlank("test"));
        assertTrue(ValidationUtil.isNotBlank(" test "));
        assertFalse(ValidationUtil.isNotBlank(""));
        assertFalse(ValidationUtil.isNotBlank("   "));
        assertFalse(ValidationUtil.isNotBlank(null));
    }

    @Test
    void testIsBlank() {
        assertFalse(ValidationUtil.isBlank("test"));
        assertTrue(ValidationUtil.isBlank(""));
        assertTrue(ValidationUtil.isBlank("   "));
        assertTrue(ValidationUtil.isBlank(null));
    }

    @Test
    void testAreEqual() {
        assertTrue(ValidationUtil.areEqual(new BigDecimal("10"), new BigDecimal("10")));
        assertTrue(ValidationUtil.areEqual(new BigDecimal("10.0"), new BigDecimal("10")));
        assertTrue(ValidationUtil.areEqual(null, null));
        assertFalse(ValidationUtil.areEqual(new BigDecimal("10"), new BigDecimal("20")));
        assertFalse(ValidationUtil.areEqual(new BigDecimal("10"), null));
        assertFalse(ValidationUtil.areEqual(null, new BigDecimal("10")));
    }
}




