package com.hhsa.document.entity;

import com.hhsa.common.infrastructure.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Document entity for storing document metadata.
 * Actual file storage is handled by storage providers (local file system, S3, etc.).
 */
@Entity
@Table(name = "documents", indexes = {
    @Index(name = "idx_documents_storage_key", columnList = "storage_key"),
    @Index(name = "idx_documents_entity_type", columnList = "entity_type, entity_id")
})
public class Document extends BaseEntity {

    @NotBlank
    @Size(max = 255)
    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    @NotBlank
    @Size(max = 100)
    @Column(name = "content_type", nullable = false, length = 100)
    private String contentType;

    @NotNull
    @Column(name = "file_size", nullable = false)
    private Long fileSize; // in bytes

    @NotBlank
    @Size(max = 500)
    @Column(name = "storage_key", nullable = false, unique = true, length = 500)
    private String storageKey; // Unique identifier for storage provider

    @Size(max = 50)
    @Column(name = "storage_provider", length = 50)
    private String storageProvider; // "local", "s3", etc.

    @Size(max = 100)
    @Column(name = "entity_type", length = 100)
    private String entityType; // e.g., "Contract", "ContractConfiguration"

    @Column(name = "entity_id")
    private Long entityId; // ID of the related entity

    @Size(max = 500)
    @Column(name = "description", length = 500)
    private String description;

    @Size(max = 50)
    @Column(name = "category", length = 50)
    private String category; // e.g., "CONTRACT", "BUDGET", "ATTACHMENT"

    @Column(name = "uploaded_by", length = 100)
    private String uploadedBy; // User ID or username

    // Getters and Setters

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getStorageKey() {
        return storageKey;
    }

    public void setStorageKey(String storageKey) {
        this.storageKey = storageKey;
    }

    public String getStorageProvider() {
        return storageProvider;
    }

    public void setStorageProvider(String storageProvider) {
        this.storageProvider = storageProvider;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(String uploadedBy) {
        this.uploadedBy = uploadedBy;
    }
}




