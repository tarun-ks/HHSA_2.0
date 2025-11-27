package com.hhsa.common.core.exception;

import java.util.List;

/**
 * Exception thrown when validation fails.
 */
public class ValidationException extends BaseException {

    private final List<ValidationError> validationErrors;

    public ValidationException(String message) {
        super("VALIDATION_ERROR", message);
        this.validationErrors = null;
    }

    public ValidationException(String message, List<ValidationError> validationErrors) {
        super("VALIDATION_ERROR", message);
        this.validationErrors = validationErrors;
    }

    public List<ValidationError> getValidationErrors() {
        return validationErrors;
    }

    /**
     * Validation error details
     */
    public static class ValidationError {
        private final String field;
        private final String message;
        private final Object rejectedValue;

        public ValidationError(String field, String message, Object rejectedValue) {
            this.field = field;
            this.message = message;
            this.rejectedValue = rejectedValue;
        }

        public String getField() {
            return field;
        }

        public String getMessage() {
            return message;
        }

        public Object getRejectedValue() {
            return rejectedValue;
        }
    }
}




