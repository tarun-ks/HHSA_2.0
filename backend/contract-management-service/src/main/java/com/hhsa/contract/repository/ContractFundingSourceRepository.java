package com.hhsa.contract.repository;

import com.hhsa.common.infrastructure.repository.BaseRepository;
import com.hhsa.contract.entity.ContractFundingSource;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContractFundingSourceRepository extends BaseRepository<ContractFundingSource, Long> {

    List<ContractFundingSource> findByContractId(Long contractId);
}




