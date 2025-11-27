import axios, { AxiosInstance } from 'axios';
import { ApiResponse, PageResponse } from './apiClient';

// Contract service API client
const contractApiClient: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_CONTRACT_API_URL || 'http://localhost:8080',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add auth token interceptor
contractApiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('accessToken');
  if (token && config.headers) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export interface Contract {
  id: number;
  contractNumber: string;
  contractTitle: string;
  contractValue: number;
  contractAmount: number;
  contractStartDate: string;
  contractEndDate: string;
  statusId: number;
  statusName: string;
  agencyId: string;
  programId?: string;
  providerId: string;
  organizationId: string;
  ePin: string;
  configurationWorkflowInstanceKey?: string;
  cofWorkflowInstanceKey?: string;
  createdAt: string;
  updatedAt: string;
}

export interface ContractCreateRequest {
  contractNumber: string;
  contractTitle: string;
  contractValue: number;
  contractAmount: number;
  contractStartDate: string;
  contractEndDate: string;
  agencyId: string;
  programId?: string;
  providerId: string;
  organizationId: string;
  ePin: string;
  procurementId?: string;
  parentContractId?: number;
  contractTypeId?: number;
}

export interface ContractConfiguration {
  id: number;
  contractId: number;
  uobc: string;
  subOc: string;
  rc: string;
  amount: number; // Total amount (sum of all fiscal years)
  fiscalYearAmounts?: Record<string, number>; // Fiscal year breakdown: { "FY12": 200000, "FY13": 200000, ... }
}

export interface ContractConfigurationRequest {
  contractId: number;
  coaAllocations: {
    uobc: string;
    subOc: string;
    rc: string;
    amount: number; // Total amount (sum of all fiscal years)
    fiscalYearAmounts?: Record<string, number>; // Fiscal year breakdown: { "FY12": 200000, "FY13": 200000, ... }
  }[];
}

export interface ContractDetail {
  contract: Contract;
  configurations: ContractConfiguration[];
}

export interface ContractBudget {
  id: number;
  contractId: number;
  fiscalYear: string;
  budgetCode: string;
  objectCode: string;
  amount: number;
}

export interface ContractBudgetRequest {
  contractId: number;
  allocations: {
    fiscalYear: string;
    budgetCode: string;
    objectCode: string;
    amount: number;
  }[];
}

/**
 * Contract service for API calls.
 */
export const contractService = {
  /**
   * Get contracts list with pagination, search, and filters
   */
  async getContracts(params?: {
    page?: number;
    size?: number;
    sortBy?: string;
    sortDirection?: string;
    statusId?: number | string;
    search?: string;
    startDate?: string;
    endDate?: string;
    minValue?: number | string;
    maxValue?: number | string;
  }): Promise<ApiResponse<PageResponse<Contract>>> {
    const queryParams = new URLSearchParams();
    if (params?.page !== undefined) queryParams.append('page', params.page.toString());
    if (params?.size !== undefined) queryParams.append('size', params.size.toString());
    if (params?.sortBy) queryParams.append('sortBy', params.sortBy);
    if (params?.sortDirection) queryParams.append('sortDirection', params.sortDirection);
    if (params?.statusId !== undefined) queryParams.append('statusId', params.statusId.toString());
    if (params?.search) queryParams.append('search', params.search);
    if (params?.startDate) queryParams.append('startDate', params.startDate);
    if (params?.endDate) queryParams.append('endDate', params.endDate);
    if (params?.minValue !== undefined) queryParams.append('minValue', params.minValue.toString());
    if (params?.maxValue !== undefined) queryParams.append('maxValue', params.maxValue.toString());

    const url = `/api/v1/contracts${queryParams.toString() ? `?${queryParams.toString()}` : ''}`;
    const response = await contractApiClient.get<ApiResponse<PageResponse<Contract>>>(url);
    return response.data;
  },

  /**
   * Get contract by ID
   */
  async getContract(id: number): Promise<ApiResponse<ContractDetail>> {
    const response = await contractApiClient.get<ApiResponse<ContractDetail>>(`/api/v1/contracts/${id}`);
    return response.data;
  },

  /**
   * Create contract
   */
  async createContract(data: ContractCreateRequest): Promise<ApiResponse<Contract>> {
    const response = await contractApiClient.post<ApiResponse<Contract>>('/api/v1/contracts', data);
    return response.data;
  },

  /**
   * Configure contract (Chart of Accounts)
   */
  async configureContract(
    contractId: number,
    data: ContractConfigurationRequest
  ): Promise<ApiResponse<ContractConfiguration[]>> {
    const response = await contractApiClient.post<ApiResponse<ContractConfiguration[]>>(
      `/api/v1/contracts/${contractId}/configure`,
      data
    );
    return response.data;
  },

  /**
   * Get contract budgets
   */
  async getContractBudgets(contractId: number): Promise<ApiResponse<ContractBudget[]>> {
    const response = await contractApiClient.get<ApiResponse<ContractBudget[]>>(
      `/api/v1/contracts/${contractId}/budgets`
    );
    return response.data;
  },

  /**
   * Save contract budgets
   */
  async saveContractBudgets(
    contractId: number,
    data: ContractBudgetRequest
  ): Promise<ApiResponse<ContractBudget[]>> {
    const response = await contractApiClient.post<ApiResponse<ContractBudget[]>>(
      `/api/v1/contracts/${contractId}/budgets`,
      data
    );
    return response.data;
  },

  /**
   * Get contract funding sources
   */
  async getContractFundingSources(
    contractId: number
  ): Promise<ApiResponse<ContractFundingSource[]>> {
    const response = await contractApiClient.get<ApiResponse<ContractFundingSource[]>>(
      `/api/v1/contracts/${contractId}/funding-sources`
    );
    return response.data;
  },

  /**
   * Save contract funding sources
   */
  async saveContractFundingSources(
    contractId: number,
    data: ContractFundingSourceRequest
  ): Promise<ApiResponse<ContractFundingSource[]>> {
    const response = await contractApiClient.post<ApiResponse<ContractFundingSource[]>>(
      `/api/v1/contracts/${contractId}/funding-sources`,
      data
    );
    return response.data;
  },

  /**
   * Get contract comments
   */
  async getContractComments(contractId: number): Promise<ApiResponse<ContractComment[]>> {
    const response = await contractApiClient.get<ApiResponse<ContractComment[]>>(
      `/api/v1/contracts/${contractId}/comments`
    );
    return response.data;
  },

  /**
   * Create a contract comment
   */
  async createContractComment(
    contractId: number,
    data: ContractCommentRequest
  ): Promise<ApiResponse<ContractComment>> {
    const response = await contractApiClient.post<ApiResponse<ContractComment>>(
      `/api/v1/contracts/${contractId}/comments`,
      data
    );
    return response.data;
  },

  /**
   * Delete a contract comment
   */
  async deleteContractComment(
    contractId: number,
    commentId: number
  ): Promise<ApiResponse<void>> {
    const response = await contractApiClient.delete<ApiResponse<void>>(
      `/api/v1/contracts/${contractId}/comments/${commentId}`
    );
    return response.data;
  },

  /**
   * Get all available budget templates
   */
  async getAllBudgetTemplates(): Promise<ApiResponse<BudgetTemplate[]>> {
    const response = await contractApiClient.get<ApiResponse<BudgetTemplate[]>>(
      '/api/v1/contracts/budget-templates'
    );
    return response.data;
  },

  /**
   * Get selected budget templates for a contract
   */
  async getContractBudgetTemplates(contractId: number): Promise<ApiResponse<BudgetTemplate[]>> {
    const response = await contractApiClient.get<ApiResponse<BudgetTemplate[]>>(
      `/api/v1/contracts/${contractId}/budget-templates`
    );
    return response.data;
  },

  /**
   * Save selected budget templates for a contract
   */
  async saveContractBudgetTemplates(
    contractId: number,
    data: ContractBudgetTemplateRequest
  ): Promise<ApiResponse<BudgetTemplate[]>> {
    const response = await contractApiClient.post<ApiResponse<BudgetTemplate[]>>(
      `/api/v1/contracts/${contractId}/budget-templates`,
      data
    );
    return response.data;
  },
};

export interface ContractFundingSource {
  id: number;
  contractId: number;
  fundingSourceId: string; // e.g., "Federal", "State", "City", "Other"
  amount: number; // Total amount (sum of all fiscal years)
  fiscalYearAmounts?: Record<string, number>; // Fiscal year breakdown: { "FY12": 50000, "FY13": 50000, ... }
}

export interface ContractFundingSourceRequest {
  contractId: number;
  fundingSourceAllocations: {
    fundingSourceId: string;
    amount: number; // Total amount (sum of all fiscal years)
    fiscalYearAmounts?: Record<string, number>; // Fiscal year breakdown: { "FY12": 50000, "FY13": 50000, ... }
  }[];
}

export interface ContractComment {
  id: number;
  contractId: number;
  taskId?: string;
  commentText: string;
  authorId: string;
  authorName?: string;
  createdAt: string;
  updatedAt: string;
}

export interface ContractCommentRequest {
  contractId: number;
  taskId?: string;
  commentText: string;
}

export interface BudgetTemplate {
  id: number;
  name: string;
  description?: string;
}

export interface ContractBudgetTemplateRequest {
  contractId: number;
  templateIds: number[];
}
