package com.hhsa.document.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Factory for selecting storage provider based on configuration.
 * Supports local file system (POC) and S3 (future).
 */
@Component
public class StorageProviderFactory {

    private final Map<String, StorageProvider> storageProviders;
    private final String defaultProvider;

    @Autowired
    public StorageProviderFactory(
            Map<String, StorageProvider> storageProviders,
            @Value("${document.storage.provider:local}") String defaultProvider) {
        this.storageProviders = storageProviders;
        this.defaultProvider = defaultProvider;
    }

    /**
     * Get storage provider by name
     */
    public StorageProvider getStorageProvider(String providerName) {
        if (providerName == null) {
            providerName = defaultProvider;
        }

        StorageProvider provider = storageProviders.get(providerName + "StorageProvider");
        if (provider == null) {
            // Try with different naming convention
            provider = storageProviders.get(providerName + "FileSystemStorageProvider");
        }
        if (provider == null) {
            // Try default naming
            provider = storageProviders.get("localFileSystemStorageProvider");
        }

        if (provider == null) {
            throw new IllegalStateException("No storage provider found: " + providerName);
        }

        return provider;
    }

    /**
     * Get default storage provider
     */
    public StorageProvider getDefaultStorageProvider() {
        return getStorageProvider(null);
    }
}




