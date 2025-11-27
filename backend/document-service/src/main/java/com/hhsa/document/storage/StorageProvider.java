package com.hhsa.document.storage;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Storage provider interface for pluggable document storage.
 * Implementations: LocalFileSystemStorageProvider, S3StorageProvider (future)
 */
public interface StorageProvider {

    /**
     * Get provider name (e.g., "local", "s3")
     */
    String getProviderName();

    /**
     * Store document and return storage key
     * @param inputStream Document input stream
     * @param fileName Original file name
     * @param contentType Content type (MIME type)
     * @return Storage key (unique identifier for the stored document)
     */
    String store(InputStream inputStream, String fileName, String contentType) throws StorageException;

    /**
     * Retrieve document as input stream
     * @param storageKey Storage key returned by store()
     * @return Document input stream
     */
    InputStream retrieve(String storageKey) throws StorageException;

    /**
     * Delete document
     * @param storageKey Storage key
     */
    void delete(String storageKey) throws StorageException;

    /**
     * Check if document exists
     * @param storageKey Storage key
     * @return true if document exists
     */
    boolean exists(String storageKey) throws StorageException;

    /**
     * Get document size in bytes
     * @param storageKey Storage key
     * @return Document size in bytes
     */
    long getSize(String storageKey) throws StorageException;
}




