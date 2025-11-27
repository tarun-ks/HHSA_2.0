package com.hhsa.contract.controller;

import com.hhsa.common.core.dto.ApiResponse;
import com.hhsa.common.core.dto.PageResponse;
import com.hhsa.common.core.util.PaginationUtil;
import com.hhsa.contract.dto.*;
import com.hhsa.contract.service.ContractService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Contract controller.
 * Provides REST endpoints for contract management.
 */
@RestController
@RequestMapping("/api/v1/contracts")
@Tag(name = "Contracts", description = "Contract management endpoints")
public class ContractController {

    private final ContractService contractService;

    public ContractController(ContractService contractService) {
        this.contractService = contractService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ACCO_STAFF', 'ACCO_MANAGER', 'ACCO_ADMIN_STAFF', 'PROGRAM_MANAGER')")
    @Operation(summary = "Create Contract", description = "Create a new contract")
    public ResponseEntity<ApiResponse<ContractDTO>> createContract(
            @Valid @RequestBody ContractCreateRequest request,
            @RequestHeader(value = "X-User-Id", required = false, defaultValue = "system") String userId) {
        try {
            ContractDTO contract = contractService.createContract(request, userId);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(contract, "Contract created successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage(), new ApiResponse.ErrorDetails("CONTRACT_CREATE_ERROR", e.getMessage())));
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Contract", description = "Get contract by ID with configurations")
    public ResponseEntity<ApiResponse<ContractDetailDTO>> getContract(@PathVariable Long id) {
        try {
            ContractDetailDTO contract = contractService.getContractDetail(id);
            return ResponseEntity.ok(ApiResponse.success(contract));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage(), new ApiResponse.ErrorDetails("CONTRACT_NOT_FOUND", e.getMessage())));
        }
    }

    @GetMapping
    @Operation(summary = "List Contracts", description = "List all contracts with pagination")
    public ResponseEntity<ApiResponse<PageResponse<ContractDTO>>> listContracts(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDirection,
            @RequestParam(required = false) Integer statusId) {
        try {
            Pageable pageable = PaginationUtil.createPageable(page, size, sortBy, sortDirection);
            Page<ContractDTO> contracts = contractService.findAll(pageable)
                .map(contract -> contractService.toDTO(contract));

            PageResponse<ContractDTO> response = PageResponse.of(contracts);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to retrieve contracts: " + e.getMessage(), new ApiResponse.ErrorDetails("QUERY_ERROR", "Failed to retrieve contracts: " + e.getMessage())));
        }
    }

    @PostMapping("/{id}/configure")
    @PreAuthorize("hasAnyRole('ACCO_STAFF', 'ACCO_MANAGER', 'ACCO_ADMIN_STAFF')")
    @Operation(summary = "Configure Contract", description = "Configure contract Chart of Accounts")
    public ResponseEntity<ApiResponse<List<ContractConfigurationDTO>>> configureContract(
            @PathVariable Long id,
            @Valid @RequestBody ContractConfigurationRequest request,
            @RequestHeader(value = "X-User-Id", required = false, defaultValue = "system") String userId) {
        try {
            request.setContractId(id);
            List<ContractConfigurationDTO> configurations = contractService.configureContract(request, userId);
            return ResponseEntity.ok(ApiResponse.success(configurations, "Contract configured successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage(), new ApiResponse.ErrorDetails("CONFIGURATION_ERROR", e.getMessage())));
        }
    }

    @GetMapping("/{id}/budgets")
    @Operation(summary = "Get Contract Budgets", description = "Get all budget allocations for a contract")
    public ResponseEntity<ApiResponse<List<ContractBudgetDTO>>> getContractBudgets(@PathVariable Long id) {
        try {
            List<ContractBudgetDTO> budgets = contractService.getContractBudgets(id);
            return ResponseEntity.ok(ApiResponse.success(budgets));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage(), new ApiResponse.ErrorDetails("BUDGET_NOT_FOUND", e.getMessage())));
        }
    }

    @GetMapping("/{id}/funding-sources")
    @Operation(summary = "Get Contract Funding Sources", description = "Get all funding source allocations for a contract")
    public ResponseEntity<ApiResponse<List<ContractFundingSourceDTO>>> getContractFundingSources(@PathVariable Long id) {
        try {
            List<ContractFundingSourceDTO> fundingSources = contractService.getContractFundingSources(id);
            return ResponseEntity.ok(ApiResponse.success(fundingSources));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage(), new ApiResponse.ErrorDetails("FUNDING_SOURCE_NOT_FOUND", e.getMessage())));
        }
    }

    @PostMapping("/{id}/funding-sources")
    @PreAuthorize("hasAnyRole('ACCO_STAFF', 'ACCO_MANAGER', 'ACCO_ADMIN_STAFF', 'FINANCE_STAFF', 'FINANCE_MANAGER')")
    @Operation(summary = "Save Contract Funding Sources", description = "Save funding source allocations for a contract (optional)")
    public ResponseEntity<ApiResponse<List<ContractFundingSourceDTO>>> saveContractFundingSources(
            @PathVariable Long id,
            @Valid @RequestBody ContractFundingSourceRequest request,
            @RequestHeader(value = "X-User-Id", required = false, defaultValue = "system") String userId) {
        try {
            request.setContractId(id);
            List<ContractFundingSourceDTO> fundingSources = contractService.saveContractFundingSources(request, userId);
            return ResponseEntity.ok(ApiResponse.success(fundingSources, "Funding sources saved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage(), new ApiResponse.ErrorDetails("FUNDING_SOURCE_ERROR", e.getMessage())));
        }
    }

    @PostMapping("/{id}/budgets")
    @PreAuthorize("hasAnyRole('ACCO_STAFF', 'ACCO_MANAGER', 'ACCO_ADMIN_STAFF', 'FINANCE_STAFF', 'FINANCE_MANAGER')")
    @Operation(summary = "Save Contract Budgets", description = "Create or update budget allocations for a contract")
    public ResponseEntity<ApiResponse<List<ContractBudgetDTO>>> saveContractBudgets(
            @PathVariable Long id,
            @Valid @RequestBody ContractBudgetRequest request,
            @RequestHeader(value = "X-User-Id", required = false, defaultValue = "system") String userId) {
        try {
            // Set contract ID from path variable
            ContractBudgetRequest updatedRequest = new ContractBudgetRequest(
                id,
                request.allocations()
            );
            List<ContractBudgetDTO> budgets = contractService.saveContractBudgets(updatedRequest, userId);
            return ResponseEntity.ok(ApiResponse.success(budgets, "Contract budgets saved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage(), new ApiResponse.ErrorDetails("BUDGET_SAVE_ERROR", e.getMessage())));
        }
    }

    @GetMapping("/{id}/comments")
    @Operation(summary = "Get Contract Comments", description = "Get all comments for a contract")
    public ResponseEntity<ApiResponse<List<ContractCommentDTO>>> getContractComments(@PathVariable Long id) {
        try {
            List<ContractCommentDTO> comments = contractService.getContractComments(id);
            return ResponseEntity.ok(ApiResponse.success(comments));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage(), new ApiResponse.ErrorDetails("COMMENTS_NOT_FOUND", e.getMessage())));
        }
    }

    @PostMapping("/{id}/comments")
    @PreAuthorize("hasAnyRole('ACCO_STAFF', 'ACCO_MANAGER', 'ACCO_ADMIN_STAFF', 'PROGRAM_MANAGER', 'FINANCE_STAFF', 'FINANCE_MANAGER')")
    @Operation(summary = "Create Contract Comment", description = "Add a comment to a contract")
    public ResponseEntity<ApiResponse<ContractCommentDTO>> createContractComment(
            @PathVariable Long id,
            @Valid @RequestBody ContractCommentRequest request,
            @RequestHeader(value = "X-User-Id", required = false, defaultValue = "system") String userId,
            @RequestHeader(value = "X-User-Name", required = false, defaultValue = "System User") String userName) {
        try {
            request.setContractId(id);
            ContractCommentDTO comment = contractService.createContractComment(request, userId, userName);
            return ResponseEntity.ok(ApiResponse.success(comment, "Comment created successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage(), new ApiResponse.ErrorDetails("COMMENT_ERROR", e.getMessage())));
        }
    }

    @DeleteMapping("/{id}/comments/{commentId}")
    @PreAuthorize("hasAnyRole('ACCO_STAFF', 'ACCO_MANAGER', 'ACCO_ADMIN_STAFF', 'PROGRAM_MANAGER', 'FINANCE_STAFF', 'FINANCE_MANAGER')")
    @Operation(summary = "Delete Contract Comment", description = "Delete a comment from a contract")
    public ResponseEntity<ApiResponse<Void>> deleteContractComment(
            @PathVariable Long id,
            @PathVariable Long commentId,
            @RequestHeader(value = "X-User-Id", required = false, defaultValue = "system") String userId) {
        try {
            contractService.deleteContractComment(commentId, userId);
            return ResponseEntity.ok(ApiResponse.success(null, "Comment deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage(), new ApiResponse.ErrorDetails("COMMENT_DELETE_ERROR", e.getMessage())));
        }
    }

    @GetMapping("/budget-templates")
    @Operation(summary = "Get All Budget Templates", description = "Get all available budget templates")
    public ResponseEntity<ApiResponse<List<BudgetTemplateDTO>>> getAllBudgetTemplates() {
        try {
            List<BudgetTemplateDTO> templates = contractService.getAllBudgetTemplates().stream()
                .map(template -> {
                    BudgetTemplateDTO dto = new BudgetTemplateDTO();
                    dto.setId(template.getId());
                    dto.setName(template.getName());
                    dto.setDescription(template.getDescription());
                    return dto;
                })
                .collect(java.util.stream.Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success(templates));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(e.getMessage(), new ApiResponse.ErrorDetails("TEMPLATE_ERROR", e.getMessage())));
        }
    }

    @GetMapping("/{id}/budget-templates")
    @Operation(summary = "Get Contract Budget Templates", description = "Get selected budget templates for a contract")
    public ResponseEntity<ApiResponse<List<BudgetTemplateDTO>>> getContractBudgetTemplates(@PathVariable Long id) {
        try {
            List<BudgetTemplateDTO> templates = contractService.getContractBudgetTemplates(id).stream()
                .map(template -> {
                    BudgetTemplateDTO dto = new BudgetTemplateDTO();
                    dto.setId(template.getId());
                    dto.setName(template.getName());
                    dto.setDescription(template.getDescription());
                    return dto;
                })
                .collect(java.util.stream.Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success(templates));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage(), new ApiResponse.ErrorDetails("TEMPLATE_NOT_FOUND", e.getMessage())));
        }
    }

    @PostMapping("/{id}/budget-templates")
    @PreAuthorize("hasAnyRole('ACCO_STAFF', 'ACCO_MANAGER', 'ACCO_ADMIN_STAFF', 'FINANCE_STAFF', 'FINANCE_MANAGER')")
    @Operation(summary = "Save Contract Budget Templates", description = "Save selected budget templates for a contract")
    public ResponseEntity<ApiResponse<List<BudgetTemplateDTO>>> saveContractBudgetTemplates(
            @PathVariable Long id,
            @Valid @RequestBody ContractBudgetTemplateRequest request,
            @RequestHeader(value = "X-User-Id", required = false, defaultValue = "system") String userId) {
        try {
            request.setContractId(id);
            List<BudgetTemplateDTO> templates = contractService.saveContractBudgetTemplates(
                id, request.getTemplateIds(), userId
            ).stream()
                .map(template -> {
                    BudgetTemplateDTO dto = new BudgetTemplateDTO();
                    dto.setId(template.getId());
                    dto.setName(template.getName());
                    dto.setDescription(template.getDescription());
                    return dto;
                })
                .collect(java.util.stream.Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success(templates, "Budget templates saved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage(), new ApiResponse.ErrorDetails("TEMPLATE_SAVE_ERROR", e.getMessage())));
        }
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update Contract Status", description = "Update contract status (called by workflow callbacks)")
    public ResponseEntity<ApiResponse<Void>> updateContractStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        try {
            Integer statusId = (Integer) request.get("statusId");
            contractService.updateContractStatus(id, statusId);
            return ResponseEntity.ok(ApiResponse.success(null, "Contract status updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage(), new ApiResponse.ErrorDetails("STATUS_UPDATE_ERROR", e.getMessage())));
        }
    }
}
