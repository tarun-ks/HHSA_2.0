package com.hhsa.contract.repository;

import com.hhsa.common.infrastructure.repository.BaseRepository;
import com.hhsa.contract.entity.ContractComment;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Contract Comment entities.
 */
@Repository
public interface ContractCommentRepository extends BaseRepository<ContractComment, Long> {

    /**
     * Find all comments for a contract (ordered by creation date, newest first)
     */
    List<ContractComment> findByContractIdOrderByCreatedAtDesc(Long contractId);

    /**
     * Find all comments for a contract and task
     */
    List<ContractComment> findByContractIdAndTaskIdOrderByCreatedAtDesc(Long contractId, String taskId);

    /**
     * Find all comments for a task
     */
    List<ContractComment> findByTaskIdOrderByCreatedAtDesc(String taskId);
}

