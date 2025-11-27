package com.hhsa.common.core.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DateUtil
 */
class DateUtilTest {

    @Test
    void testFormatDate() {
        LocalDate date = LocalDate.of(2024, 1, 15);
        String formatted = DateUtil.formatDate(date);
        assertEquals("2024-01-15", formatted);
        assertNull(DateUtil.formatDate(null));
    }

    @Test
    void testParseDate() {
        LocalDate date = DateUtil.parseDate("2024-01-15");
        assertEquals(LocalDate.of(2024, 1, 15), date);
        assertNull(DateUtil.parseDate(null));
        assertNull(DateUtil.parseDate(""));
    }

    @Test
    void testParseDateInvalidFormat() {
        assertThrows(IllegalArgumentException.class, () -> {
            DateUtil.parseDate("01/15/2024");
        });
    }

    @Test
    void testIsPast() {
        LocalDate pastDate = LocalDate.now().minusDays(1);
        LocalDate futureDate = LocalDate.now().plusDays(1);
        
        assertTrue(DateUtil.isPast(pastDate));
        assertFalse(DateUtil.isPast(futureDate));
        assertFalse(DateUtil.isPast(null));
    }

    @Test
    void testIsFuture() {
        LocalDate pastDate = LocalDate.now().minusDays(1);
        LocalDate futureDate = LocalDate.now().plusDays(1);
        
        assertFalse(DateUtil.isFuture(pastDate));
        assertTrue(DateUtil.isFuture(futureDate));
        assertFalse(DateUtil.isFuture(null));
    }

    @Test
    void testIsValidDateRange() {
        LocalDate start = LocalDate.of(2024, 1, 1);
        LocalDate end = LocalDate.of(2024, 1, 31);
        
        assertTrue(DateUtil.isValidDateRange(start, end));
        assertTrue(DateUtil.isValidDateRange(start, start));
        assertFalse(DateUtil.isValidDateRange(end, start));
        assertFalse(DateUtil.isValidDateRange(null, end));
        assertFalse(DateUtil.isValidDateRange(start, null));
    }

    @Test
    void testToday() {
        LocalDate today = DateUtil.today();
        assertEquals(LocalDate.now(), today);
    }

    @Test
    void testNow() {
        LocalDateTime now = DateUtil.now();
        assertNotNull(now);
        // Just verify it's not null and is recent (within last second)
        assertTrue(now.isAfter(LocalDateTime.now().minusSeconds(1)));
    }
}




