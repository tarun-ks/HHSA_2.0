package com.hhsa.contract.repository;

import com.hhsa.common.infrastructure.repository.BaseRepository;
import com.hhsa.contract.entity.Contract;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContractRepository extends BaseRepository<Contract, Long> {

    Optional<Contract> findByContractNumber(String contractNumber);

    List<Contract> findByStatusId(Integer statusId);

    List<Contract> findByAgencyId(String agencyId);

    List<Contract> findByProviderId(String providerId);

    List<Contract> findByOrganizationId(String organizationId);

    List<Contract> findByParentContractId(Long parentContractId);
}




