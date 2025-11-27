package com.hhsa.common.core.exception;

/**
 * Exception thrown when a requested resource is not found.
 */
public class ResourceNotFoundException extends BaseException {

    public ResourceNotFoundException(String message) {
        super("RESOURCE_NOT_FOUND", message);
    }

    public ResourceNotFoundException(String resourceType, Object id) {
        super("RESOURCE_NOT_FOUND", String.format("%s with id %s not found", resourceType, id));
    }
}




