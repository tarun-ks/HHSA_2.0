package com.hhsa.contract.entity;

import com.hhsa.common.infrastructure.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Contract Comment entity.
 * Stores comments related to contracts and workflow tasks.
 */
@Entity
@Table(name = "contract_comments", indexes = {
    @Index(name = "idx_contract_comments_contract", columnList = "contract_id"),
    @Index(name = "idx_contract_comments_task", columnList = "task_id"),
    @Index(name = "idx_contract_comments_created", columnList = "created_at")
})
public class ContractComment extends BaseEntity {

    @NotNull
    @Column(name = "contract_id", nullable = false)
    private Long contractId;

    @Size(max = 100)
    @Column(name = "task_id", length = 100)
    private String taskId; // Optional: Link to workflow task

    @NotBlank
    @Column(name = "comment_text", nullable = false, columnDefinition = "TEXT")
    private String commentText;

    @NotBlank
    @Size(max = 100)
    @Column(name = "author_id", nullable = false, length = 100)
    private String authorId;

    @Size(max = 200)
    @Column(name = "author_name", length = 200)
    private String authorName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id", insertable = false, updatable = false)
    private Contract contract;

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

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public Contract getContract() {
        return contract;
    }

    public void setContract(Contract contract) {
        this.contract = contract;
    }
}

