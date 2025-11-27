package com.hhsa.contract.repository;

import com.hhsa.common.infrastructure.repository.BaseRepository;
import com.hhsa.contract.entity.ContractCOAFiscalYear;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Contract COA Fiscal Year entities.
 */
@Repository
public interface ContractCOAFiscalYearRepository extends BaseRepository<ContractCOAFiscalYear, Long> {

    /**
     * Find all fiscal year entries for a contract configuration
     */
    List<ContractCOAFiscalYear> findByContractConfigurationId(Long contractConfigurationId);

    /**
     * Find all fiscal year entries for multiple contract configurations
     */
    List<ContractCOAFiscalYear> findByContractConfigurationIdIn(List<Long> contractConfigurationIds);

    /**
     * Delete all fiscal year entries for a contract configuration
     */
    void deleteByContractConfigurationId(Long contractConfigurationId);
}

