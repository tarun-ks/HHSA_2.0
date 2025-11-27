package com.hhsa.common.core.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Utility class for pagination operations.
 */
public class PaginationUtil {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 100;

    /**
     * Create Pageable from page and size parameters
     */
    public static Pageable createPageable(Integer page, Integer size) {
        int pageNumber = (page != null && page >= 0) ? page : DEFAULT_PAGE;
        int pageSize = (size != null && size > 0) ? Math.min(size, MAX_SIZE) : DEFAULT_SIZE;
        return PageRequest.of(pageNumber, pageSize);
    }

    /**
     * Create Pageable with sorting
     */
    public static Pageable createPageable(Integer page, Integer size, String sortBy, String sortDirection) {
        int pageNumber = (page != null && page >= 0) ? page : DEFAULT_PAGE;
        int pageSize = (size != null && size > 0) ? Math.min(size, MAX_SIZE) : DEFAULT_SIZE;

        Sort sort = createSort(sortBy, sortDirection);
        return PageRequest.of(pageNumber, pageSize, sort);
    }

    /**
     * Create Sort from sortBy and sortDirection
     */
    public static Sort createSort(String sortBy, String sortDirection) {
        if (sortBy == null || sortBy.trim().isEmpty()) {
            return Sort.unsorted();
        }

        Sort.Direction direction = Sort.Direction.ASC;
        if (sortDirection != null && sortDirection.equalsIgnoreCase("desc")) {
            direction = Sort.Direction.DESC;
        }

        return Sort.by(direction, sortBy);
    }

    /**
     * Create Sort with multiple fields
     */
    public static Sort createSort(String[] sortFields, String sortDirection) {
        if (sortFields == null || sortFields.length == 0) {
            return Sort.unsorted();
        }

        Sort.Direction direction = Sort.Direction.ASC;
        if (sortDirection != null && sortDirection.equalsIgnoreCase("desc")) {
            direction = Sort.Direction.DESC;
        }

        Sort.Order[] orders = new Sort.Order[sortFields.length];
        for (int i = 0; i < sortFields.length; i++) {
            orders[i] = new Sort.Order(direction, sortFields[i]);
        }

        return Sort.by(orders);
    }

    /**
     * Validate page number (must be >= 0)
     */
    public static boolean isValidPage(Integer page) {
        return page == null || page >= 0;
    }

    /**
     * Validate page size (must be > 0 and <= MAX_SIZE)
     */
    public static boolean isValidSize(Integer size) {
        return size == null || (size > 0 && size <= MAX_SIZE);
    }
}




