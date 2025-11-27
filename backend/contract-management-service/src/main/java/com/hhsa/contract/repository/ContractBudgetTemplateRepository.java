package com.hhsa.contract.repository;

import com.hhsa.common.infrastructure.repository.BaseRepository;
import com.hhsa.contract.entity.ContractBudgetTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Contract Budget Template entities.
 */
@Repository
public interface ContractBudgetTemplateRepository extends BaseRepository<ContractBudgetTemplate, Long> {

    /**
     * Find all budget templates for a contract
     */
    List<ContractBudgetTemplate> findByContractId(Long contractId);

    /**
     * Find by contract and template
     */
    boolean existsByContractIdAndBudgetTemplateId(Long contractId, Long budgetTemplateId);

    /**
     * Delete all budget templates for a contract
     */
    void deleteByContractId(Long contractId);
}

