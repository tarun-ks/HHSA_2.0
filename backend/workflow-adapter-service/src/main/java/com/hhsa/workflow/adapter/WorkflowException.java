package com.hhsa.workflow.adapter;

/**
 * Exception thrown by workflow adapter operations
 */
public class WorkflowException extends Exception {

    public WorkflowException(String message) {
        super(message);
    }

    public WorkflowException(String message, Throwable cause) {
        super(message, cause);
    }
}




