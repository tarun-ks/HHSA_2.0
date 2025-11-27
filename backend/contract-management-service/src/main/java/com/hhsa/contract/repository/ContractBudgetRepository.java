package com.hhsa.contract.repository;

import com.hhsa.common.infrastructure.repository.BaseRepository;
import com.hhsa.contract.entity.ContractBudget;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Contract Budget entities.
 */
@Repository
public interface ContractBudgetRepository extends BaseRepository<ContractBudget, Long> {

    /**
     * Find all budgets for a contract
     */
    List<ContractBudget> findByContractId(Long contractId);

    /**
     * Find all budgets for a contract and fiscal year
     */
    List<ContractBudget> findByContractIdAndFiscalYear(Long contractId, String fiscalYear);

    /**
     * Delete all budgets for a contract
     */
    void deleteByContractId(Long contractId);
}

