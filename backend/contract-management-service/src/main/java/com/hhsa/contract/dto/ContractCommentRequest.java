package com.hhsa.contract.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for creating a contract comment
 */
public class ContractCommentRequest {

    @NotNull(message = "Contract ID is required")
    private Long contractId;

    @Size(max = 100)
    private String taskId; // Optional: Link to workflow task

    @NotBlank(message = "Comment text is required")
    @Size(max = 5000, message = "Comment text must not exceed 5000 characters")
    private String commentText;

    // Getters and Setters

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }
}

