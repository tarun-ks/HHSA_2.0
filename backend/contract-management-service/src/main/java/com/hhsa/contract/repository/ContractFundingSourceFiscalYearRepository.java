package com.hhsa.contract.repository;

import com.hhsa.common.infrastructure.repository.BaseRepository;
import com.hhsa.contract.entity.ContractFundingSourceFiscalYear;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Contract Funding Source Fiscal Year entities.
 */
@Repository
public interface ContractFundingSourceFiscalYearRepository extends BaseRepository<ContractFundingSourceFiscalYear, Long> {

    /**
     * Find all fiscal year entries for a funding source
     */
    List<ContractFundingSourceFiscalYear> findByContractFundingSourceId(Long contractFundingSourceId);

    /**
     * Find all fiscal year entries for multiple funding sources
     */
    List<ContractFundingSourceFiscalYear> findByContractFundingSourceIdIn(List<Long> contractFundingSourceIds);

    /**
     * Delete all fiscal year entries for a funding source
     */
    void deleteByContractFundingSourceId(Long contractFundingSourceId);
}

