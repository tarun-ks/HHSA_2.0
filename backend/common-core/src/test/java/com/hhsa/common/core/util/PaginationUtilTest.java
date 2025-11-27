package com.hhsa.common.core.util;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PaginationUtil
 */
class PaginationUtilTest {

    @Test
    void testCreatePageable() {
        Pageable pageable = PaginationUtil.createPageable(0, 20);
        assertEquals(0, pageable.getPageNumber());
        assertEquals(20, pageable.getPageSize());
    }

    @Test
    void testCreatePageableWithDefaults() {
        Pageable pageable = PaginationUtil.createPageable(null, null);
        assertEquals(0, pageable.getPageNumber());
        assertEquals(20, pageable.getPageSize());
    }

    @Test
    void testCreatePageableWithMaxSize() {
        Pageable pageable = PaginationUtil.createPageable(0, 200);
        assertEquals(100, pageable.getPageSize()); // Should be capped at MAX_SIZE
    }

    @Test
    void testCreatePageableWithSorting() {
        Pageable pageable = PaginationUtil.createPageable(0, 20, "name", "asc");
        assertEquals(0, pageable.getPageNumber());
        assertEquals(20, pageable.getPageSize());
        assertTrue(pageable.getSort().getOrderFor("name").getDirection().isAscending());
    }

    @Test
    void testCreatePageableWithDescSorting() {
        Pageable pageable = PaginationUtil.createPageable(0, 20, "name", "desc");
        assertTrue(pageable.getSort().getOrderFor("name").getDirection().isDescending());
    }

    @Test
    void testCreateSort() {
        Sort sort = PaginationUtil.createSort(new String[]{"name"}, "asc");
        assertNotNull(sort);
        assertTrue(sort.getOrderFor("name").getDirection().isAscending());
    }

    @Test
    void testCreateSortWithNullField() {
        Sort sort = PaginationUtil.createSort((String) null, "asc");
        assertTrue(sort.isUnsorted());
    }

    @Test
    void testIsValidPage() {
        assertTrue(PaginationUtil.isValidPage(0));
        assertTrue(PaginationUtil.isValidPage(10));
        assertTrue(PaginationUtil.isValidPage(null));
        assertFalse(PaginationUtil.isValidPage(-1));
    }

    @Test
    void testIsValidSize() {
        assertTrue(PaginationUtil.isValidSize(1));
        assertTrue(PaginationUtil.isValidSize(50));
        assertTrue(PaginationUtil.isValidSize(100));
        assertTrue(PaginationUtil.isValidSize(null));
        assertFalse(PaginationUtil.isValidSize(0));
        assertFalse(PaginationUtil.isValidSize(101));
    }
}

