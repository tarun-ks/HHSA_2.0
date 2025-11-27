package com.hhsa.contract.repository;

import com.hhsa.common.infrastructure.repository.BaseRepository;
import com.hhsa.contract.entity.BudgetTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Budget Template entities.
 */
@Repository
public interface BudgetTemplateRepository extends BaseRepository<BudgetTemplate, Long> {

    /**
     * Find all budget templates (non-deleted)
     */
    List<BudgetTemplate> findByDeletedFalseOrderByNameAsc();

    /**
     * Find by name
     */
    Optional<BudgetTemplate> findByName(String name);
}

