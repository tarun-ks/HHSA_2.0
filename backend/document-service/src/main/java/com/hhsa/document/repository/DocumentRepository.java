package com.hhsa.document.repository;

import com.hhsa.common.infrastructure.repository.BaseRepository;
import com.hhsa.document.entity.Document;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends BaseRepository<Document, Long> {

    Optional<Document> findByStorageKey(String storageKey);

    List<Document> findByEntityTypeAndEntityId(String entityType, Long entityId);

    List<Document> findByCategory(String category);

    List<Document> findByUploadedBy(String uploadedBy);
}




