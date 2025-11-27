package com.hhsa.document.controller;

import com.hhsa.common.core.dto.ApiResponse;
import com.hhsa.common.core.dto.PageResponse;
import com.hhsa.common.core.util.PaginationUtil;
import com.hhsa.document.dto.DocumentDTO;
import com.hhsa.document.service.DocumentService;
import com.hhsa.document.storage.StorageException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Document controller.
 * Provides document upload, download, and management endpoints.
 */
@RestController
@RequestMapping("/api/v1/documents")
@Tag(name = "Documents", description = "Document management endpoints")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload Document", description = "Upload a document file")
    public ResponseEntity<ApiResponse<DocumentDTO>> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "entityType", required = false) String entityType,
            @RequestParam(value = "entityId", required = false) Long entityId,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "description", required = false) String description,
            @RequestHeader(value = "X-User-Id", required = false, defaultValue = "system") String uploadedBy) {

        try {
            DocumentDTO document = documentService.uploadDocument(
                file, entityType, entityId, category, description, uploadedBy
            );
            return ResponseEntity.ok(ApiResponse.success(document, "Document uploaded successfully"));
        } catch (StorageException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to upload document: " + e.getMessage(), 
                    new ApiResponse.ErrorDetails("STORAGE_ERROR", "Failed to upload document: " + e.getMessage())));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to upload document: " + e.getMessage(), 
                    new ApiResponse.ErrorDetails("UPLOAD_ERROR", "Failed to upload document: " + e.getMessage())));
        }
    }

    @GetMapping("/{id}/download")
    @Operation(summary = "Download Document", description = "Download a document by ID")
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long id) {
        try {
            Resource resource = documentService.downloadDocument(id);
            DocumentDTO document = documentService.getRepository().findById(id)
                .map(doc -> {
                    DocumentDTO dto = new DocumentDTO();
                    dto.setFileName(doc.getFileName());
                    dto.setContentType(doc.getContentType());
                    return dto;
                })
                .orElseThrow();

            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(document.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                    "attachment; filename=\"" + document.getFileName() + "\"")
                .body(resource);
        } catch (StorageException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Document", description = "Delete a document by ID")
    public ResponseEntity<ApiResponse<Void>> deleteDocument(@PathVariable Long id) {
        try {
            documentService.deleteDocument(id);
            return ResponseEntity.ok(ApiResponse.success(null, "Document deleted successfully"));
        } catch (StorageException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to delete document: " + e.getMessage(), 
                    new ApiResponse.ErrorDetails("STORAGE_ERROR", "Failed to delete document: " + e.getMessage())));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to delete document: " + e.getMessage(), 
                    new ApiResponse.ErrorDetails("DELETE_ERROR", "Failed to delete document: " + e.getMessage())));
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Document", description = "Get document metadata by ID")
    public ResponseEntity<ApiResponse<DocumentDTO>> getDocument(@PathVariable Long id) {
        try {
            DocumentDTO document = documentService.getRepository().findById(id)
                .map(doc -> {
                    DocumentDTO dto = new DocumentDTO();
                    dto.setId(doc.getId());
                    dto.setFileName(doc.getFileName());
                    dto.setContentType(doc.getContentType());
                    dto.setFileSize(doc.getFileSize());
                    dto.setStorageKey(doc.getStorageKey());
                    dto.setStorageProvider(doc.getStorageProvider());
                    dto.setEntityType(doc.getEntityType());
                    dto.setEntityId(doc.getEntityId());
                    dto.setDescription(doc.getDescription());
                    dto.setCategory(doc.getCategory());
                    dto.setUploadedBy(doc.getUploadedBy());
                    dto.setCreatedAt(doc.getCreatedAt());
                    dto.setUpdatedAt(doc.getUpdatedAt());
                    return dto;
                })
                .orElseThrow();

            return ResponseEntity.ok(ApiResponse.success(document));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Document not found", 
                    new ApiResponse.ErrorDetails("NOT_FOUND", "Document not found")));
        }
    }

    @GetMapping("/entity/{entityType}/{entityId}")
    @Operation(summary = "Get Documents by Entity", description = "Get all documents for a specific entity")
    public ResponseEntity<ApiResponse<List<DocumentDTO>>> getDocumentsByEntity(
            @PathVariable String entityType,
            @PathVariable Long entityId) {
        try {
            List<DocumentDTO> documents = documentService.findByEntity(entityType, entityId);
            return ResponseEntity.ok(ApiResponse.success(documents));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to retrieve documents: " + e.getMessage(), 
                    new ApiResponse.ErrorDetails("QUERY_ERROR", "Failed to retrieve documents: " + e.getMessage())));
        }
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Get Documents by Category", description = "Get all documents in a category")
    public ResponseEntity<ApiResponse<List<DocumentDTO>>> getDocumentsByCategory(
            @PathVariable String category) {
        try {
            List<DocumentDTO> documents = documentService.findByCategory(category);
            return ResponseEntity.ok(ApiResponse.success(documents));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to retrieve documents: " + e.getMessage(), 
                    new ApiResponse.ErrorDetails("QUERY_ERROR", "Failed to retrieve documents: " + e.getMessage())));
        }
    }

    @GetMapping
    @Operation(summary = "List Documents", description = "List all documents with pagination")
    public ResponseEntity<ApiResponse<PageResponse<DocumentDTO>>> listDocuments(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDirection) {
        try {
            Pageable pageable = PaginationUtil.createPageable(page, size, sortBy, sortDirection);
            Page<DocumentDTO> documents = documentService.findAll(pageable)
                .map(doc -> {
                    DocumentDTO dto = new DocumentDTO();
                    dto.setId(doc.getId());
                    dto.setFileName(doc.getFileName());
                    dto.setContentType(doc.getContentType());
                    dto.setFileSize(doc.getFileSize());
                    dto.setStorageKey(doc.getStorageKey());
                    dto.setStorageProvider(doc.getStorageProvider());
                    dto.setEntityType(doc.getEntityType());
                    dto.setEntityId(doc.getEntityId());
                    dto.setDescription(doc.getDescription());
                    dto.setCategory(doc.getCategory());
                    dto.setUploadedBy(doc.getUploadedBy());
                    dto.setCreatedAt(doc.getCreatedAt());
                    dto.setUpdatedAt(doc.getUpdatedAt());
                    return dto;
                });

            PageResponse<DocumentDTO> response = PageResponse.of(documents);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to retrieve documents: " + e.getMessage(), 
                    new ApiResponse.ErrorDetails("QUERY_ERROR", "Failed to retrieve documents: " + e.getMessage())));
        }
    }
}


