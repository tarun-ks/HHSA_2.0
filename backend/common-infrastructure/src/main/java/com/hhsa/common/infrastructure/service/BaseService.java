package com.hhsa.common.infrastructure.service;

import com.hhsa.common.infrastructure.entity.BaseEntity;
import com.hhsa.common.core.exception.ResourceNotFoundException;
import com.hhsa.common.infrastructure.repository.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Base service interface providing common CRUD operations.
 * All domain services should extend this interface.
 *
 * @param <T> Entity type extending BaseEntity
 * @param <ID> ID type (typically Long)
 * @param <R> Repository type extending BaseRepository
 */
public interface BaseService<T extends BaseEntity, ID, R extends BaseRepository<T, ID>> {

    /**
     * Get the repository instance
     */
    R getRepository();

    /**
     * Find entity by ID
     * @throws ResourceNotFoundException if entity not found
     */
    default T findById(ID id) {
        return getRepository().findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(getEntityName(), id));
    }

    /**
     * Find entity by ID (optional)
     */
    default Optional<T> findByIdOptional(ID id) {
        return getRepository().findById(id);
    }

    /**
     * Find all entities
     */
    default List<T> findAll() {
        return getRepository().findAll();
    }

    /**
     * Find all entities with pagination
     */
    default Page<T> findAll(Pageable pageable) {
        return getRepository().findAll(pageable);
    }

    /**
     * Save entity (create or update)
     */
    default T save(T entity) {
        return getRepository().save(entity);
    }

    /**
     * Save all entities
     */
    default List<T> saveAll(Iterable<T> entities) {
        return getRepository().saveAll(entities);
    }

    /**
     * Delete entity (soft delete)
     */
    default void deleteById(ID id) {
        T entity = findById(id);
        entity.softDelete();
        getRepository().save(entity);
    }

    /**
     * Delete entity (soft delete)
     */
    default void delete(T entity) {
        entity.softDelete();
        getRepository().save(entity);
    }

    /**
     * Check if entity exists
     */
    default boolean existsById(ID id) {
        return getRepository().existsById(id);
    }

    /**
     * Count all entities
     */
    default long count() {
        return getRepository().count();
    }

    /**
     * Get entity name for error messages
     */
    default String getEntityName() {
        return "Entity";
    }
}

