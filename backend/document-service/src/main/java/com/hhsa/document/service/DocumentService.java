package com.hhsa.document.service;

import com.hhsa.common.infrastructure.service.BaseService;
import com.hhsa.document.dto.DocumentDTO;
import com.hhsa.document.entity.Document;
import com.hhsa.document.repository.DocumentRepository;
import com.hhsa.document.storage.StorageException;
import com.hhsa.document.storage.StorageProvider;
import com.hhsa.document.storage.StorageProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Document service implementation.
 * Handles document upload, download, and metadata management.
 */
@Service
@Transactional
public class DocumentService implements BaseService<Document, Long, DocumentRepository> {

    private static final Logger logger = LoggerFactory.getLogger(DocumentService.class);

    private final DocumentRepository documentRepository;
    private final StorageProviderFactory storageProviderFactory;

    public DocumentService(
            DocumentRepository documentRepository,
            StorageProviderFactory storageProviderFactory) {
        this.documentRepository = documentRepository;
        this.storageProviderFactory = storageProviderFactory;
    }

    @Override
    public DocumentRepository getRepository() {
        return documentRepository;
    }

    @Override
    public String getEntityName() {
        return "Document";
    }

    /**
     * Upload document
     */
    public DocumentDTO uploadDocument(
            MultipartFile file,
            String entityType,
            Long entityId,
            String category,
            String description,
            String uploadedBy) throws StorageException, IOException {

        logger.debug("Uploading document: {} ({} bytes)", file.getOriginalFilename(), file.getSize());

        // Get storage provider
        StorageProvider storageProvider = storageProviderFactory.getDefaultStorageProvider();

        // Store file
        String storageKey = storageProvider.store(
            file.getInputStream(),
            file.getOriginalFilename(),
            file.getContentType()
        );

        // Create document entity
        Document document = new Document();
        document.setFileName(file.getOriginalFilename());
        document.setContentType(file.getContentType());
        document.setFileSize(file.getSize());
        document.setStorageKey(storageKey);
        document.setStorageProvider(storageProvider.getProviderName());
        document.setEntityType(entityType);
        document.setEntityId(entityId);
        document.setCategory(category);
        document.setDescription(description);
        document.setUploadedBy(uploadedBy);

        // Save document metadata
        document = save(document);

        logger.info("Document uploaded successfully: {} (ID: {})", storageKey, document.getId());
        return toDTO(document);
    }

    /**
     * Download document
     */
    public Resource downloadDocument(Long documentId) throws StorageException {
        Document document = findById(documentId);
        return downloadDocumentByStorageKey(document.getStorageKey());
    }

    /**
     * Download document by storage key
     */
    public Resource downloadDocumentByStorageKey(String storageKey) throws StorageException {
        Document document = documentRepository.findByStorageKey(storageKey)
            .orElseThrow(() -> new StorageException("Document not found: " + storageKey));

        StorageProvider storageProvider = storageProviderFactory.getStorageProvider(document.getStorageProvider());
        InputStream inputStream = storageProvider.retrieve(storageKey);
        
        if (inputStream == null) {
            throw new StorageException("Failed to retrieve document stream: " + storageKey);
        }

        return new InputStreamResource(inputStream) {
            @Override
            public String getFilename() {
                return document.getFileName();
            }

            @Override
            public long contentLength() {
                return document.getFileSize();
            }
        };
    }

    /**
     * Delete document
     */
    public void deleteDocument(Long documentId) throws StorageException {
        Document document = findById(documentId);

        // Delete from storage
        StorageProvider storageProvider = storageProviderFactory.getStorageProvider(document.getStorageProvider());
        storageProvider.delete(document.getStorageKey());

        // Soft delete metadata
        delete(document);

        logger.info("Document deleted: {} (ID: {})", document.getStorageKey(), documentId);
    }

    /**
     * Find documents by entity
     */
    public List<DocumentDTO> findByEntity(String entityType, Long entityId) {
        List<Document> documents = documentRepository.findByEntityTypeAndEntityId(entityType, entityId);
        return documents.stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    /**
     * Find documents by category
     */
    public List<DocumentDTO> findByCategory(String category) {
        List<Document> documents = documentRepository.findByCategory(category);
        return documents.stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    /**
     * Find documents by uploader
     */
    public List<DocumentDTO> findByUploader(String uploadedBy) {
        List<Document> documents = documentRepository.findByUploadedBy(uploadedBy);
        return documents.stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    /**
     * Convert entity to DTO
     */
    private DocumentDTO toDTO(Document document) {
        DocumentDTO dto = new DocumentDTO();
        dto.setId(document.getId());
        dto.setFileName(document.getFileName());
        dto.setContentType(document.getContentType());
        dto.setFileSize(document.getFileSize());
        dto.setStorageKey(document.getStorageKey());
        dto.setStorageProvider(document.getStorageProvider());
        dto.setEntityType(document.getEntityType());
        dto.setEntityId(document.getEntityId());
        dto.setDescription(document.getDescription());
        dto.setCategory(document.getCategory());
        dto.setUploadedBy(document.getUploadedBy());
        dto.setCreatedAt(document.getCreatedAt());
        dto.setUpdatedAt(document.getUpdatedAt());
        return dto;
    }
}

