package com.hhsa.document.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Local file system storage provider implementation.
 * Used for POC and local development.
 */
@Component("localFileSystemStorageProvider")
public class LocalFileSystemStorageProvider implements StorageProvider {

    private static final Logger logger = LoggerFactory.getLogger(LocalFileSystemStorageProvider.class);

    private final String basePath;
    private final boolean createDirectories;

    public LocalFileSystemStorageProvider(
            @Value("${document.storage.local.base-path:./documents}") String basePath,
            @Value("${document.storage.local.create-directories:true}") boolean createDirectories) {
        this.basePath = basePath;
        this.createDirectories = createDirectories;
        initializeStorage();
    }

    private void initializeStorage() {
        try {
            Path storagePath = Paths.get(basePath);
            if (createDirectories && !Files.exists(storagePath)) {
                Files.createDirectories(storagePath);
                logger.info("Created document storage directory: {}", basePath);
            }
        } catch (IOException e) {
            logger.error("Failed to initialize storage directory: {}", basePath, e);
            throw new RuntimeException("Failed to initialize storage directory", e);
        }
    }

    @Override
    public String getProviderName() {
        return "local";
    }

    @Override
    public String store(InputStream inputStream, String fileName, String contentType) throws StorageException {
        try {
            // Generate unique storage key: date/uuid-filename
            String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String uniqueId = UUID.randomUUID().toString();
            String sanitizedFileName = sanitizeFileName(fileName);
            String storageKey = datePath + "/" + uniqueId + "-" + sanitizedFileName;

            // Create full path
            Path fullPath = Paths.get(basePath, storageKey);
            Path parentDir = fullPath.getParent();

            // Create parent directories if needed
            if (createDirectories && parentDir != null && !Files.exists(parentDir)) {
                Files.createDirectories(parentDir);
            }

            // Copy input stream to file
            Files.copy(inputStream, fullPath, StandardCopyOption.REPLACE_EXISTING);

            logger.debug("Stored document: {} -> {}", fileName, storageKey);
            return storageKey;

        } catch (IOException e) {
            logger.error("Failed to store document: {}", fileName, e);
            throw new StorageException("Failed to store document: " + fileName, e);
        }
    }

    @Override
    public InputStream retrieve(String storageKey) throws StorageException {
        try {
            Path fullPath = Paths.get(basePath, storageKey);
            
            if (!Files.exists(fullPath)) {
                throw new StorageException("Document not found: " + storageKey);
            }

            return new FileInputStream(fullPath.toFile());

        } catch (FileNotFoundException e) {
            logger.error("Document not found: {}", storageKey, e);
            throw new StorageException("Document not found: " + storageKey, e);
        } catch (IOException e) {
            logger.error("Failed to retrieve document: {}", storageKey, e);
            throw new StorageException("Failed to retrieve document: " + storageKey, e);
        }
    }

    @Override
    public void delete(String storageKey) throws StorageException {
        try {
            Path fullPath = Paths.get(basePath, storageKey);
            
            if (!Files.exists(fullPath)) {
                logger.warn("Document not found for deletion: {}", storageKey);
                return;
            }

            Files.delete(fullPath);
            logger.debug("Deleted document: {}", storageKey);

        } catch (IOException e) {
            logger.error("Failed to delete document: {}", storageKey, e);
            throw new StorageException("Failed to delete document: " + storageKey, e);
        }
    }

    @Override
    public boolean exists(String storageKey) throws StorageException {
        try {
            Path fullPath = Paths.get(basePath, storageKey);
            return Files.exists(fullPath);
        } catch (Exception e) {
            logger.error("Failed to check document existence: {}", storageKey, e);
            throw new StorageException("Failed to check document existence: " + storageKey, e);
        }
    }

    @Override
    public long getSize(String storageKey) throws StorageException {
        try {
            Path fullPath = Paths.get(basePath, storageKey);
            
            if (!Files.exists(fullPath)) {
                throw new StorageException("Document not found: " + storageKey);
            }

            return Files.size(fullPath);

        } catch (IOException e) {
            logger.error("Failed to get document size: {}", storageKey, e);
            throw new StorageException("Failed to get document size: " + storageKey, e);
        }
    }

    /**
     * Sanitize file name to prevent directory traversal and other security issues
     */
    private String sanitizeFileName(String fileName) {
        if (fileName == null) {
            return "unnamed";
        }
        // Remove path separators and other dangerous characters
        return fileName.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}




