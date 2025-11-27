package com.hhsa.contract.service;

import com.hhsa.common.core.dto.ApiResponse;
import com.hhsa.common.core.dto.PageResponse;
import com.hhsa.common.core.exception.ResourceNotFoundException;
import com.hhsa.common.core.exception.ValidationException;
import com.hhsa.common.infrastructure.service.BaseService;
import com.hhsa.common.core.util.ValidationUtil;
import com.hhsa.contract.dto.*;
import com.hhsa.contract.entity.Contract;
import com.hhsa.contract.entity.ContractConfiguration;
import com.hhsa.contract.entity.ContractBudget;
import com.hhsa.contract.entity.ContractCOAFiscalYear;
import com.hhsa.contract.entity.ContractFundingSource;
import com.hhsa.contract.entity.ContractFundingSourceFiscalYear;
import com.hhsa.contract.entity.ContractComment;
import com.hhsa.contract.entity.BudgetTemplate;
import com.hhsa.contract.entity.ContractBudgetTemplate;
import com.hhsa.contract.repository.ContractRepository;
import com.hhsa.contract.repository.ContractConfigurationRepository;
import com.hhsa.contract.repository.ContractBudgetRepository;
import com.hhsa.contract.repository.ContractCOAFiscalYearRepository;
import com.hhsa.contract.repository.ContractFundingSourceRepository;
import com.hhsa.contract.repository.ContractFundingSourceFiscalYearRepository;
import com.hhsa.contract.repository.ContractCommentRepository;
import com.hhsa.contract.repository.BudgetTemplateRepository;
import com.hhsa.contract.repository.ContractBudgetTemplateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Contract service implementation.
 * Handles contract creation, configuration, and management.
 */
@Service
@Transactional
public class ContractService implements BaseService<Contract, Long, ContractRepository> {

    private static final Logger log = LoggerFactory.getLogger(ContractService.class);

    private final ContractRepository contractRepository;
    private final ContractConfigurationRepository configurationRepository;
    private final ContractBudgetRepository budgetRepository;
    private final ContractCOAFiscalYearRepository coaFiscalYearRepository;
    private final ContractFundingSourceRepository fundingSourceRepository;
    private final ContractFundingSourceFiscalYearRepository fundingSourceFiscalYearRepository;
    private final ContractCommentRepository commentRepository;
    private final BudgetTemplateRepository budgetTemplateRepository;
    private final ContractBudgetTemplateRepository contractBudgetTemplateRepository;
    private final WorkflowIntegrationService workflowIntegrationService;

    public ContractService(
            ContractRepository contractRepository,
            ContractConfigurationRepository configurationRepository,
            ContractBudgetRepository budgetRepository,
            ContractCOAFiscalYearRepository coaFiscalYearRepository,
            ContractFundingSourceRepository fundingSourceRepository,
            ContractFundingSourceFiscalYearRepository fundingSourceFiscalYearRepository,
            ContractCommentRepository commentRepository,
            BudgetTemplateRepository budgetTemplateRepository,
            ContractBudgetTemplateRepository contractBudgetTemplateRepository,
            WorkflowIntegrationService workflowIntegrationService) {
        this.contractRepository = contractRepository;
        this.configurationRepository = configurationRepository;
        this.budgetRepository = budgetRepository;
        this.coaFiscalYearRepository = coaFiscalYearRepository;
        this.fundingSourceRepository = fundingSourceRepository;
        this.fundingSourceFiscalYearRepository = fundingSourceFiscalYearRepository;
        this.commentRepository = commentRepository;
        this.budgetTemplateRepository = budgetTemplateRepository;
        this.contractBudgetTemplateRepository = contractBudgetTemplateRepository;
        this.workflowIntegrationService = workflowIntegrationService;
    }

    @Override
    public ContractRepository getRepository() {
        return contractRepository;
    }

    @Override
    public String getEntityName() {
        return "Contract";
    }

    /**
     * Create a new contract
     */
    public ContractDTO createContract(ContractCreateRequest request, String userId) {
        // Validate E-PIN (in real implementation, would call APT system)
        validateEPin(request.getEPin());

        // Check if contract number already exists
        if (contractRepository.findByContractNumber(request.getContractNumber()).isPresent()) {
            throw new ValidationException("Contract number already exists");
        }

        // Validate dates
        if (request.getContractStartDate().isAfter(request.getContractEndDate())) {
            throw new ValidationException("Contract start date must be before end date");
        }

        // Create contract entity
        Contract contract = new Contract();
        contract.setContractNumber(request.getContractNumber());
        contract.setContractTitle(request.getContractTitle());
        contract.setContractValue(request.getContractValue());
        contract.setContractAmount(request.getContractAmount());
        contract.setContractStartDate(request.getContractStartDate());
        contract.setContractEndDate(request.getContractEndDate());
        contract.setStatusId(59); // Pending Configuration
        contract.setAgencyId(request.getAgencyId());
        contract.setProgramId(request.getProgramId());
        contract.setProviderId(request.getProviderId());
        contract.setOrganizationId(request.getOrganizationId());
        contract.setEPin(request.getEPin());
        contract.setProcurementId(request.getProcurementId());
        contract.setParentContractId(request.getParentContractId());
        contract.setContractTypeId(request.getContractTypeId());
        contract.setCreatedBy(userId);

        contract = save(contract);

        // Launch WF302: Contract Configuration workflow (if COF needed)
        // In production, this would be conditional based on business rules
        String workflowInstanceKey = workflowIntegrationService.launchConfigurationWorkflow(contract, userId);
        if (workflowInstanceKey != null) {
            contract.setConfigurationWorkflowInstanceKey(workflowInstanceKey);
            save(contract);
        }

        return toDTO(contract);
    }

    /**
     * Configure contract (Chart of Accounts)
     */
    public List<ContractConfigurationDTO> configureContract(
            ContractConfigurationRequest request, String userId) {
        
        Contract contract = findById(request.getContractId());

        // Validate contract is in correct status
        if (contract.getStatusId() != 59) {
            throw new ValidationException("Contract must be in Pending Configuration status");
        }

        // Validate total allocation equals contract value
        BigDecimal totalAllocation = request.getCoaAllocations().stream()
            .map(ContractConfigurationRequest.COAAllocation::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (!ValidationUtil.areEqual(totalAllocation, contract.getContractValue())) {
            throw new ValidationException(
                String.format("Total COA allocation (%s) must equal contract value (%s)",
                    totalAllocation, contract.getContractValue()));
        }

        // Validate no duplicate COA combinations
        long uniqueCombinations = request.getCoaAllocations().stream()
            .map(a -> a.getUobc() + "|" + a.getSubOc() + "|" + a.getRc())
            .distinct()
            .count();

        if (uniqueCombinations != request.getCoaAllocations().size()) {
            throw new ValidationException("Duplicate COA combinations are not allowed");
        }

        // Delete existing configurations (cascade will delete fiscal year entries)
        List<ContractConfiguration> existing = configurationRepository.findByContractId(contract.getId());
        // Delete fiscal year entries first (if cascade doesn't work)
        existing.forEach(config -> {
            coaFiscalYearRepository.deleteByContractConfigurationId(config.getId());
        });
        existing.forEach(configurationRepository::delete);

        // Create new configurations with fiscal year breakdown
        List<ContractConfiguration> configurations = request.getCoaAllocations().stream()
            .map(allocation -> {
                ContractConfiguration config = new ContractConfiguration();
                config.setContractId(contract.getId());
                config.setUobc(allocation.getUobc());
                config.setSubOc(allocation.getSubOc());
                config.setRc(allocation.getRc());
                config.setAmount(allocation.getAmount());
                config.setCreatedBy(userId);
                return config;
            })
            .collect(Collectors.toList());

        configurations = configurationRepository.saveAll(configurations);

        // Save fiscal year breakdowns for each configuration
        for (int i = 0; i < configurations.size(); i++) {
            ContractConfiguration config = configurations.get(i);
            ContractConfigurationRequest.COAAllocation allocation = request.getCoaAllocations().get(i);
            
            // If fiscal year amounts are provided, use them; otherwise, allocate evenly to all fiscal years
            Map<String, BigDecimal> fiscalYearAmounts = allocation.getFiscalYearAmounts();
            if (fiscalYearAmounts != null && !fiscalYearAmounts.isEmpty()) {
                // Validate fiscal year amounts sum equals total amount
                BigDecimal fiscalYearTotal = fiscalYearAmounts.values().stream()
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                if (!ValidationUtil.areEqual(fiscalYearTotal, allocation.getAmount())) {
                    throw new ValidationException(
                        String.format("Fiscal year amounts for COA %s-%s-%s must sum to %s, but sum is %s",
                            allocation.getUobc(), allocation.getSubOc(), allocation.getRc(),
                            allocation.getAmount(), fiscalYearTotal));
                }
                
                // Save fiscal year breakdowns
                fiscalYearAmounts.forEach((fiscalYear, amount) -> {
                    ContractCOAFiscalYear fyEntry = new ContractCOAFiscalYear();
                    fyEntry.setContractConfigurationId(config.getId());
                    fyEntry.setFiscalYear(fiscalYear);
                    fyEntry.setAmount(amount);
                    fyEntry.setCreatedBy(userId);
                    coaFiscalYearRepository.save(fyEntry);
                });
            } else {
                // Default: allocate evenly across standard fiscal years (FY12-FY16)
                // This is a fallback - in production, you might want to require fiscal year breakdown
                String[] standardFiscalYears = {"FY12", "FY13", "FY14", "FY15", "FY16"};
                BigDecimal amountPerYear = allocation.getAmount()
                    .divide(BigDecimal.valueOf(standardFiscalYears.length), 2, java.math.RoundingMode.HALF_UP);
                
                for (String fiscalYear : standardFiscalYears) {
                    ContractCOAFiscalYear fyEntry = new ContractCOAFiscalYear();
                    fyEntry.setContractConfigurationId(config.getId());
                    fyEntry.setFiscalYear(fiscalYear);
                    fyEntry.setAmount(amountPerYear);
                    fyEntry.setCreatedBy(userId);
                    coaFiscalYearRepository.save(fyEntry);
                }
            }
        }

        // Update contract status to Pending COF (60)
        contract.setStatusId(60);
        contract.setUpdatedBy(userId);
        save(contract);

        // Launch WF303: Contract Certification of Funds workflow
        String cofWorkflowInstanceKey = workflowIntegrationService.launchCOFWorkflow(contract, userId);
        if (cofWorkflowInstanceKey != null) {
            contract.setCofWorkflowInstanceKey(cofWorkflowInstanceKey);
            save(contract);
        }

        return configurations.stream()
            .map(this::toConfigurationDTO)
            .collect(Collectors.toList());
    }

    /**
     * Get contract with configurations
     */
    public ContractDetailDTO getContractDetail(Long contractId) {
        Contract contract = findById(contractId);
        List<ContractConfiguration> configurations = configurationRepository.findByContractId(contractId);

        ContractDetailDTO dto = new ContractDetailDTO();
        dto.setContract(toDTO(contract));
        dto.setConfigurations(configurations.stream()
            .map(this::toConfigurationDTO)
            .collect(Collectors.toList()));

        return dto;
    }

    /**
     * Validate E-PIN (placeholder - would call APT system in production)
     */
    private void validateEPin(String ePin) {
        if (ePin == null || ePin.trim().isEmpty()) {
            throw new ValidationException("E-PIN is required");
        }
        // In production, this would call APT system to validate E-PIN
        // For now, just check it's not empty
    }

    /**
     * Convert entity to DTO
     */
    public ContractDTO toDTO(Contract contract) {
        ContractDTO dto = new ContractDTO();
        dto.setId(contract.getId());
        dto.setContractNumber(contract.getContractNumber());
        dto.setContractTitle(contract.getContractTitle());
        dto.setContractValue(contract.getContractValue());
        dto.setContractAmount(contract.getContractAmount());
        dto.setContractStartDate(contract.getContractStartDate());
        dto.setContractEndDate(contract.getContractEndDate());
        dto.setStatusId(contract.getStatusId());
        dto.setStatusName(getStatusName(contract.getStatusId()));
        dto.setAgencyId(contract.getAgencyId());
        dto.setProgramId(contract.getProgramId());
        dto.setProviderId(contract.getProviderId());
        dto.setOrganizationId(contract.getOrganizationId());
        dto.setEPin(contract.getEPin());
        dto.setRegistrationFlag(contract.getRegistrationFlag());
        dto.setProcurementId(contract.getProcurementId());
        dto.setParentContractId(contract.getParentContractId());
        dto.setContractTypeId(contract.getContractTypeId());
        dto.setConfigurationWorkflowInstanceKey(contract.getConfigurationWorkflowInstanceKey());
        dto.setCofWorkflowInstanceKey(contract.getCofWorkflowInstanceKey());
        dto.setCreatedAt(contract.getCreatedAt());
        dto.setUpdatedAt(contract.getUpdatedAt());
        return dto;
    }

    private ContractConfigurationDTO toConfigurationDTO(ContractConfiguration config) {
        ContractConfigurationDTO dto = new ContractConfigurationDTO();
        dto.setId(config.getId());
        dto.setContractId(config.getContractId());
        dto.setUobc(config.getUobc());
        dto.setSubOc(config.getSubOc());
        dto.setRc(config.getRc());
        dto.setAmount(config.getAmount());
        
        // Load fiscal year breakdown
        List<ContractCOAFiscalYear> fiscalYears = coaFiscalYearRepository.findByContractConfigurationId(config.getId());
        Map<String, BigDecimal> fiscalYearAmounts = new HashMap<>();
        for (ContractCOAFiscalYear fy : fiscalYears) {
            fiscalYearAmounts.put(fy.getFiscalYear(), fy.getAmount());
        }
        dto.setFiscalYearAmounts(fiscalYearAmounts);
        
        return dto;
    }

    /**
     * Get contract funding sources
     */
    public List<ContractFundingSourceDTO> getContractFundingSources(Long contractId) {
        List<ContractFundingSource> fundingSources = fundingSourceRepository.findByContractId(contractId);
        return fundingSources.stream()
            .map(this::toFundingSourceDTO)
            .collect(Collectors.toList());
    }

    /**
     * Save contract funding sources
     */
    public List<ContractFundingSourceDTO> saveContractFundingSources(
            ContractFundingSourceRequest request, String userId) {
        
        Contract contract = findById(request.getContractId());

        // Validate total funding source allocation (optional - can be less than contract value)
        BigDecimal totalAllocation = request.getFundingSourceAllocations().stream()
            .map(ContractFundingSourceRequest.FundingSourceAllocation::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Delete existing funding sources
        List<ContractFundingSource> existing = fundingSourceRepository.findByContractId(contract.getId());
        existing.forEach(fs -> {
            fundingSourceFiscalYearRepository.deleteByContractFundingSourceId(fs.getId());
        });
        existing.forEach(fundingSourceRepository::delete);

        // Create new funding sources with fiscal year breakdown
        List<ContractFundingSource> fundingSources = request.getFundingSourceAllocations().stream()
            .map(allocation -> {
                ContractFundingSource fs = new ContractFundingSource();
                fs.setContractId(contract.getId());
                fs.setFundingSourceId(allocation.getFundingSourceId());
                fs.setAmount(allocation.getAmount());
                fs.setCreatedBy(userId);
                return fs;
            })
            .collect(Collectors.toList());

        fundingSources = fundingSourceRepository.saveAll(fundingSources);

        // Save fiscal year breakdowns for each funding source
        for (int i = 0; i < fundingSources.size(); i++) {
            ContractFundingSource fs = fundingSources.get(i);
            ContractFundingSourceRequest.FundingSourceAllocation allocation = request.getFundingSourceAllocations().get(i);
            
            Map<String, BigDecimal> fiscalYearAmounts = allocation.getFiscalYearAmounts();
            if (fiscalYearAmounts != null && !fiscalYearAmounts.isEmpty()) {
                // Validate fiscal year amounts sum equals total amount
                BigDecimal fiscalYearTotal = fiscalYearAmounts.values().stream()
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                if (!ValidationUtil.areEqual(fiscalYearTotal, allocation.getAmount())) {
                    throw new ValidationException(
                        String.format("Fiscal year amounts for funding source %s must sum to %s, but sum is %s",
                            allocation.getFundingSourceId(), allocation.getAmount(), fiscalYearTotal));
                }
                
                // Save fiscal year breakdowns
                fiscalYearAmounts.forEach((fiscalYear, amount) -> {
                    ContractFundingSourceFiscalYear fyEntry = new ContractFundingSourceFiscalYear();
                    fyEntry.setContractFundingSourceId(fs.getId());
                    fyEntry.setFiscalYear(fiscalYear);
                    fyEntry.setAmount(amount);
                    fyEntry.setCreatedBy(userId);
                    fundingSourceFiscalYearRepository.save(fyEntry);
                });
            } else {
                // Default: allocate evenly across standard fiscal years (FY12-FY16)
                String[] standardFiscalYears = {"FY12", "FY13", "FY14", "FY15", "FY16"};
                BigDecimal amountPerYear = allocation.getAmount()
                    .divide(BigDecimal.valueOf(standardFiscalYears.length), 2, java.math.RoundingMode.HALF_UP);
                
                for (String fiscalYear : standardFiscalYears) {
                    ContractFundingSourceFiscalYear fyEntry = new ContractFundingSourceFiscalYear();
                    fyEntry.setContractFundingSourceId(fs.getId());
                    fyEntry.setFiscalYear(fiscalYear);
                    fyEntry.setAmount(amountPerYear);
                    fyEntry.setCreatedBy(userId);
                    fundingSourceFiscalYearRepository.save(fyEntry);
                }
            }
        }

        return fundingSources.stream()
            .map(this::toFundingSourceDTO)
            .collect(Collectors.toList());
    }

    /**
     * Convert funding source entity to DTO
     */
    private ContractFundingSourceDTO toFundingSourceDTO(ContractFundingSource fs) {
        ContractFundingSourceDTO dto = new ContractFundingSourceDTO();
        dto.setId(fs.getId());
        dto.setContractId(fs.getContractId());
        dto.setFundingSourceId(fs.getFundingSourceId());
        dto.setAmount(fs.getAmount());
        
        // Load fiscal year breakdown
        List<ContractFundingSourceFiscalYear> fiscalYears = fundingSourceFiscalYearRepository.findByContractFundingSourceId(fs.getId());
        Map<String, BigDecimal> fiscalYearAmounts = new HashMap<>();
        for (ContractFundingSourceFiscalYear fy : fiscalYears) {
            fiscalYearAmounts.put(fy.getFiscalYear(), fy.getAmount());
        }
        dto.setFiscalYearAmounts(fiscalYearAmounts);
        
        return dto;
    }

    /**
     * Get contract comments
     */
    public List<ContractCommentDTO> getContractComments(Long contractId) {
        List<ContractComment> comments = commentRepository.findByContractIdOrderByCreatedAtDesc(contractId);
        return comments.stream()
            .map(this::toCommentDTO)
            .collect(Collectors.toList());
    }

    /**
     * Create a contract comment
     */
    public ContractCommentDTO createContractComment(ContractCommentRequest request, String userId, String userName) {
        Contract contract = findById(request.getContractId());

        ContractComment comment = new ContractComment();
        comment.setContractId(contract.getId());
        comment.setTaskId(request.getTaskId());
        comment.setCommentText(request.getCommentText());
        comment.setAuthorId(userId);
        comment.setAuthorName(userName);
        comment.setCreatedBy(userId);

        comment = commentRepository.save(comment);
        log.info("Comment created: contract={}, author={}", contract.getId(), userId);

        return toCommentDTO(comment);
    }

    /**
     * Delete a contract comment
     */
    public void deleteContractComment(Long commentId, String userId) {
        ContractComment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new ResourceNotFoundException("Comment not found: " + commentId));

        // Soft delete
        comment.setDeleted(true);
        comment.setUpdatedBy(userId);
        commentRepository.save(comment);
        log.info("Comment deleted: commentId={}, deletedBy={}", commentId, userId);
    }

    /**
     * Convert comment entity to DTO
     */
    private ContractCommentDTO toCommentDTO(ContractComment comment) {
        ContractCommentDTO dto = new ContractCommentDTO();
        dto.setId(comment.getId());
        dto.setContractId(comment.getContractId());
        dto.setTaskId(comment.getTaskId());
        dto.setCommentText(comment.getCommentText());
        dto.setAuthorId(comment.getAuthorId());
        dto.setAuthorName(comment.getAuthorName());
        dto.setCreatedAt(comment.getCreatedAt() != null ? comment.getCreatedAt().toString() : null);
        dto.setUpdatedAt(comment.getUpdatedAt() != null ? comment.getUpdatedAt().toString() : null);
        return dto;
    }

    /**
     * Get all available budget templates
     */
    public List<BudgetTemplate> getAllBudgetTemplates() {
        return budgetTemplateRepository.findByDeletedFalseOrderByNameAsc();
    }

    /**
     * Get selected budget templates for a contract
     */
    public List<BudgetTemplate> getContractBudgetTemplates(Long contractId) {
        List<ContractBudgetTemplate> contractTemplates = contractBudgetTemplateRepository.findByContractId(contractId);
        return contractTemplates.stream()
            .map(ContractBudgetTemplate::getBudgetTemplate)
            .filter(template -> template != null && !template.getDeleted())
            .collect(Collectors.toList());
    }

    /**
     * Save selected budget templates for a contract
     */
    public List<BudgetTemplate> saveContractBudgetTemplates(Long contractId, List<Long> templateIds, String userId) {
        Contract contract = findById(contractId);

        // Delete existing templates
        contractBudgetTemplateRepository.deleteByContractId(contract.getId());

        // Add new templates
        List<ContractBudgetTemplate> contractTemplates = templateIds.stream()
            .map(templateId -> {
                BudgetTemplate template = budgetTemplateRepository.findById(templateId)
                    .orElseThrow(() -> new ResourceNotFoundException("Budget template not found: " + templateId));
                
                ContractBudgetTemplate contractTemplate = new ContractBudgetTemplate();
                contractTemplate.setContractId(contract.getId());
                contractTemplate.setBudgetTemplateId(templateId);
                contractTemplate.setCreatedBy(userId);
                return contractTemplate;
            })
            .collect(Collectors.toList());

        contractBudgetTemplateRepository.saveAll(contractTemplates);
        log.info("Budget templates saved for contract: contract={}, templates={}", contractId, templateIds);

        return contractTemplates.stream()
            .map(ContractBudgetTemplate::getBudgetTemplate)
            .collect(Collectors.toList());
    }

    /**
     * Update contract status (called by workflow callbacks)
     */
    public void updateContractStatus(Long contractId, Integer statusId) {
        Contract contract = findById(contractId);
        contract.setStatusId(statusId);
        save(contract);
        log.info("Contract status updated: contract={}, status={}", contractId, statusId);
    }

    /**
     * Get contract budgets
     */
    public List<ContractBudgetDTO> getContractBudgets(Long contractId) {
        Contract contract = findById(contractId);
        List<ContractBudget> budgets = budgetRepository.findByContractId(contractId);
        return budgets.stream()
            .map(this::toBudgetDTO)
            .collect(Collectors.toList());
    }

    /**
     * Create or update contract budgets
     */
    public List<ContractBudgetDTO> saveContractBudgets(
            ContractBudgetRequest request, String userId) {
        
        Contract contract = findById(request.contractId());

        // Validate fiscal year is provided for all allocations
        request.allocations().forEach(allocation -> {
            if (allocation.fiscalYear() == null || allocation.fiscalYear().trim().isEmpty()) {
                throw new ValidationException("Fiscal year is required for all budget allocations");
            }
        });

        // Delete existing budgets
        budgetRepository.deleteByContractId(contract.getId());

        // Create new budgets
        List<ContractBudget> budgets = request.allocations().stream()
            .map(allocation -> {
                ContractBudget budget = new ContractBudget();
                budget.setContractId(contract.getId());
                budget.setFiscalYear(allocation.fiscalYear());
                budget.setBudgetCode(allocation.budgetCode());
                budget.setObjectCode(allocation.objectCode());
                budget.setAmount(allocation.amount());
                budget.setCreatedBy(userId);
                return budget;
            })
            .collect(Collectors.toList());

        budgets = budgetRepository.saveAll(budgets);

        return budgets.stream()
            .map(this::toBudgetDTO)
            .collect(Collectors.toList());
    }

    /**
     * Convert ContractBudget entity to DTO
     */
    private ContractBudgetDTO toBudgetDTO(ContractBudget budget) {
        return new ContractBudgetDTO(
            budget.getId(),
            budget.getContractId(),
            budget.getFiscalYear(),
            budget.getBudgetCode(),
            budget.getObjectCode(),
            budget.getAmount()
        );
    }

    /**
     * Get status name from status ID
     */
    private String getStatusName(Integer statusId) {
        return switch (statusId) {
            case 59 -> "Pending Configuration";
            case 60 -> "Pending COF";
            case 61 -> "Pending Registration";
            case 62 -> "Registered";
            case 67 -> "Suspended";
            case 68 -> "Closed";
            case 69 -> "Cancelled";
            default -> "Unknown";
        };
    }
}

