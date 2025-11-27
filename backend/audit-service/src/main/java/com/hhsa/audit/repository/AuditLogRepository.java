package com.hhsa.audit.repository;

import com.hhsa.audit.entity.AuditLog;
import com.hhsa.common.infrastructure.repository.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository for audit log entities.
 */
@Repository
public interface AuditLogRepository extends BaseRepository<AuditLog, Long> {

    /**
     * Find audit logs by entity type and entity ID
     */
    @Query("SELECT a FROM AuditLog a WHERE a.entityType = :entityType AND a.entityId = :entityId AND a.deleted = false ORDER BY a.createdAt DESC")
    Page<AuditLog> findByEntityTypeAndEntityId(
        @Param("entityType") String entityType,
        @Param("entityId") Long entityId,
        Pageable pageable
    );

    /**
     * Find audit logs by user ID
     */
    @Query("SELECT a FROM AuditLog a WHERE a.userId = :userId AND a.deleted = false ORDER BY a.createdAt DESC")
    Page<AuditLog> findByUserId(@Param("userId") String userId, Pageable pageable);

    /**
     * Find audit logs by action
     */
    @Query("SELECT a FROM AuditLog a WHERE a.action = :action AND a.deleted = false ORDER BY a.createdAt DESC")
    Page<AuditLog> findByAction(@Param("action") String action, Pageable pageable);
}

