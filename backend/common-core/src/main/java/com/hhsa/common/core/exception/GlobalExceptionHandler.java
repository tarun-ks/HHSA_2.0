package com.hhsa.common.core.exception;

import com.hhsa.common.core.dto.ApiResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Global exception handler for all REST controllers.
 * Provides consistent error response format across all microservices.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {
        logger.warn("Resource not found: {}", ex.getMessage());
        
        ApiResponse.ErrorDetails errorDetails = new ApiResponse.ErrorDetails(
            ex.getErrorCode(),
            ex.getMessage()
        );
        
        ApiResponse<Object> response = ApiResponse.error(ex.getMessage(), errorDetails);
        response.setPath(request.getDescription(false).replace("uri=", ""));
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationException(
            ValidationException ex, WebRequest request) {
        logger.warn("Validation error: {}", ex.getMessage());
        
        List<ApiResponse.ErrorDetails> errorDetailsList = new ArrayList<>();
        if (ex.getValidationErrors() != null) {
            for (ValidationException.ValidationError validationError : ex.getValidationErrors()) {
                errorDetailsList.add(new ApiResponse.ErrorDetails(
                    "VALIDATION_ERROR",
                    validationError.getField(),
                    validationError.getMessage()
                ));
            }
        }
        
        ApiResponse<Object> response = ApiResponse.error(ex.getMessage());
        if (!errorDetailsList.isEmpty()) {
            // For now, use first error. In future, we can extend to support multiple errors
            response.setError(errorDetailsList.get(0));
        }
        response.setPath(request.getDescription(false).replace("uri=", ""));
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, WebRequest request) {
        logger.warn("Method argument validation error: {}", ex.getMessage());
        
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        List<ApiResponse.ErrorDetails> errorDetailsList = fieldErrors.stream()
            .map(error -> new ApiResponse.ErrorDetails(
                "VALIDATION_ERROR",
                error.getField(),
                error.getDefaultMessage()
            ))
            .collect(Collectors.toList());
        
        ApiResponse<Object> response = ApiResponse.error("Validation failed");
        if (!errorDetailsList.isEmpty()) {
            response.setError(errorDetailsList.get(0));
        }
        response.setPath(request.getDescription(false).replace("uri=", ""));
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleConstraintViolation(
            ConstraintViolationException ex, WebRequest request) {
        logger.warn("Constraint violation: {}", ex.getMessage());
        
        List<ApiResponse.ErrorDetails> errorDetailsList = ex.getConstraintViolations().stream()
            .map(violation -> new ApiResponse.ErrorDetails(
                "VALIDATION_ERROR",
                getPropertyPath(violation),
                violation.getMessage()
            ))
            .collect(Collectors.toList());
        
        ApiResponse<Object> response = ApiResponse.error("Validation failed");
        if (!errorDetailsList.isEmpty()) {
            response.setError(errorDetailsList.get(0));
        }
        response.setPath(request.getDescription(false).replace("uri=", ""));
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiResponse<Object>> handleBaseException(
            BaseException ex, WebRequest request) {
        logger.error("Base exception: {}", ex.getMessage(), ex);
        
        ApiResponse.ErrorDetails errorDetails = new ApiResponse.ErrorDetails(
            ex.getErrorCode() != null ? ex.getErrorCode() : "INTERNAL_ERROR",
            ex.getMessage()
        );
        
        ApiResponse<Object> response = ApiResponse.error(ex.getMessage(), errorDetails);
        response.setPath(request.getDescription(false).replace("uri=", ""));
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Object>> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, WebRequest request) {
        logger.warn("Invalid request body: {}", ex.getMessage());
        
        String message = "Invalid request format";
        if (ex.getMessage() != null && ex.getMessage().contains("JSON")) {
            message = "Invalid JSON format in request body";
        } else if (ex.getMessage() != null && ex.getMessage().contains("BigDecimal")) {
            message = "Invalid number format. Please ensure amounts are valid numbers";
        }
        
        ApiResponse.ErrorDetails errorDetails = new ApiResponse.ErrorDetails(
            "INVALID_REQUEST",
            message
        );
        
        ApiResponse<Object> response = ApiResponse.error(message, errorDetails);
        response.setPath(request.getDescription(false).replace("uri=", ""));
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(
            Exception ex, WebRequest request) {
        logger.error("Unexpected error: {}", ex.getMessage(), ex);
        
        ApiResponse.ErrorDetails errorDetails = new ApiResponse.ErrorDetails(
            "INTERNAL_ERROR",
            "An unexpected error occurred"
        );
        
        ApiResponse<Object> response = ApiResponse.error("An unexpected error occurred", errorDetails);
        response.setPath(request.getDescription(false).replace("uri=", ""));
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    private String getPropertyPath(ConstraintViolation<?> violation) {
        String path = violation.getPropertyPath().toString();
        return path.contains(".") ? path.substring(path.lastIndexOf('.') + 1) : path;
    }
}




