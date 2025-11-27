package com.hhsa.contract.repository;

import com.hhsa.common.infrastructure.repository.BaseRepository;
import com.hhsa.contract.entity.ContractConfiguration;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContractConfigurationRepository extends BaseRepository<ContractConfiguration, Long> {

    List<ContractConfiguration> findByContractId(Long contractId);

    boolean existsByContractIdAndUobcAndSubOcAndRc(Long contractId, String uobc, String subOc, String rc);
}




