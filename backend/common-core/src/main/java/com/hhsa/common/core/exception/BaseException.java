package com.hhsa.common.core.exception;

/**
 * Base exception class for all custom exceptions in the application.
 * All domain-specific exceptions should extend this class.
 */
public class BaseException extends RuntimeException {

    private final String errorCode;
    private final Object[] args;

    public BaseException(String message) {
        super(message);
        this.errorCode = null;
        this.args = null;
    }

    public BaseException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = null;
        this.args = null;
    }

    public BaseException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.args = null;
    }

    public BaseException(String errorCode, String message, Object... args) {
        super(message);
        this.errorCode = errorCode;
        this.args = args;
    }

    public BaseException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.args = null;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public Object[] getArgs() {
        return args;
    }
}




