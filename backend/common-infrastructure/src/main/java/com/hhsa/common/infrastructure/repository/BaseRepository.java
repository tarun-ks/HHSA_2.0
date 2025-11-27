package com.hhsa.common.infrastructure.repository;

import com.hhsa.common.infrastructure.entity.BaseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Base repository interface providing common CRUD operations with soft delete support.
 * All domain repositories should extend this interface.
 *
 * @param <T> Entity type extending BaseEntity
 * @param <ID> ID type (typically Long)
 */
@NoRepositoryBean
public interface BaseRepository<T extends BaseEntity, ID> extends JpaRepository<T, ID> {

    /**
     * Find entity by ID, excluding soft-deleted entities
     */
    @Query("SELECT e FROM #{#entityName} e WHERE e.id = :id AND e.deleted = false")
    Optional<T> findById(@Param("id") ID id);

    /**
     * Find all entities, excluding soft-deleted entities
     */
    @Query("SELECT e FROM #{#entityName} e WHERE e.deleted = false")
    List<T> findAll();

    /**
     * Find all entities with pagination, excluding soft-deleted entities
     */
    @Query("SELECT e FROM #{#entityName} e WHERE e.deleted = false")
    Page<T> findAll(Pageable pageable);

    /**
     * Soft delete entity by ID
     */
    @Modifying
    @Query("UPDATE #{#entityName} e SET e.deleted = true WHERE e.id = :id")
    void softDeleteById(@Param("id") ID id);

    /**
     * Check if entity exists and is not soft-deleted
     */
    @Query("SELECT COUNT(e) > 0 FROM #{#entityName} e WHERE e.id = :id AND e.deleted = false")
    boolean existsById(@Param("id") ID id);

    /**
     * Count all non-deleted entities
     */
    @Query("SELECT COUNT(e) FROM #{#entityName} e WHERE e.deleted = false")
    long count();
}

