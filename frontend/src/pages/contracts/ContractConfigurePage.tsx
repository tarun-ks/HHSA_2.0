import { useState, useEffect, useMemo } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { Button } from '../../components/atoms/Button';
import { 
  contractService, 
  ContractConfigurationRequest
} from '../../services/contractService';
import { useToast } from '../../hooks/useToast';
import { 
  type COAAllocation 
} from '../../frameworks/business-rules/rules/contract/contractConfigurationRules';
import { Tabs, type TabItem } from '../../frameworks/ui';
import {
  ContractFinancialsTab,
  ContractConfigBudgetsTab,
  ContractConfigDocumentsTab,
  ContractConfigCommentsTab,
  ContractConfigWorkflowTab,
} from './components/tabs';

// COAAllocation type imported from business rules

/**
 * Contract configuration page.
 * Allows users to configure contract Chart of Accounts using an editable grid.
 */
export const ContractConfigurePage = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const toast = useToast();
  const queryClient = useQueryClient();
  const contractId = id ? parseInt(id) : 0;

  const [allocations, setAllocations] = useState<COAAllocation[]>([]);
  const [fundingSources, setFundingSources] = useState<any[]>([]);

  const { data: contractData, isLoading } = useQuery({
    queryKey: ['contract', contractId],
    queryFn: () => contractService.getContract(contractId),
    enabled: !!contractId,
  });

  const { data: fundingSourcesData } = useQuery({
    queryKey: ['contract-funding-sources', contractId],
    queryFn: () => contractService.getContractFundingSources(contractId),
    enabled: !!contractId,
  });

  const contract = contractData?.data?.contract;
  const contractValue = contract?.contractValue || 0;

  const configureMutation = useMutation({
    mutationFn: (data: ContractConfigurationRequest) =>
      contractService.configureContract(contractId, data),
    onSuccess: (response) => {
      if (response.success) {
        toast.success('Contract configured successfully!');
        queryClient.invalidateQueries({ queryKey: ['contract', contractId] });
        queryClient.invalidateQueries({ queryKey: ['contracts'] });
        navigate(`/contracts/${contractId}`);
      } else {
        toast.error(response.error?.description || 'Failed to configure contract');
      }
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.error?.description || 'An error occurred');
    },
  });

  // Load existing configurations into editable grid
  useEffect(() => {
    if (contractData?.data?.configurations && contractData.data.configurations.length > 0) {
      setAllocations(
        contractData.data.configurations.map((config) => {
          const fiscalYearAmounts = config.fiscalYearAmounts || {};
          return {
            uobc: config.uobc,
            subOc: config.subOc,
            rc: config.rc,
            amount: config.amount,
            fiscalYearAmounts,
            // Flatten fiscal year amounts for easier editing
            fy12: fiscalYearAmounts['FY12'] || 0,
            fy13: fiscalYearAmounts['FY13'] || 0,
            fy14: fiscalYearAmounts['FY14'] || 0,
            fy15: fiscalYearAmounts['FY15'] || 0,
            fy16: fiscalYearAmounts['FY16'] || 0,
          };
        })
      );
    }
  }, [contractData]);

  // Load existing funding sources
  useEffect(() => {
    if (fundingSourcesData?.data && fundingSourcesData.data.length > 0) {
      setFundingSources(
        fundingSourcesData.data.map((fs) => {
          const fiscalYearAmounts = fs.fiscalYearAmounts || {};
          return {
            fundingSourceId: fs.fundingSourceId,
            amount: fs.amount,
            fiscalYearAmounts,
            // Flatten fiscal year amounts for easier editing
            fy12: fiscalYearAmounts['FY12'] || 0,
            fy13: fiscalYearAmounts['FY13'] || 0,
            fy14: fiscalYearAmounts['FY14'] || 0,
            fy15: fiscalYearAmounts['FY15'] || 0,
            fy16: fiscalYearAmounts['FY16'] || 0,
          };
        })
      );
    }
  }, [fundingSourcesData]);

  // Business rules validation
  const ruleContext = useMemo(
    () => ({ contractValue, allRows: allocations }),
    [contractValue, allocations]
  );
  
  // Calculate total allocation
  const totalAllocation = allocations.reduce((sum, alloc: any) => {
    const fiscalYearTotal = (alloc.fy12 || 0) + (alloc.fy13 || 0) + (alloc.fy14 || 0) + 
                            (alloc.fy15 || 0) + (alloc.fy16 || 0);
    return sum + (fiscalYearTotal || alloc.amount || 0);
  }, 0);
  
  // Check if all allocations are valid
  const isValid = useMemo(() => {
    if (allocations.length === 0) return false;
    
    // Check for duplicates
    const coaKeys = allocations.map(a => `${a.uobc}-${a.subOc}-${a.rc}`);
    const uniqueKeys = new Set(coaKeys);
    if (coaKeys.length !== uniqueKeys.size) return false;
    
    // Check all required fields
    const allFieldsValid = allocations.every(a => {
      const alloc = a as any; // Allow access to flattened fiscal year properties
      return a.uobc && a.subOc && a.rc && (a.amount > 0 || (alloc.fy12 || alloc.fy13 || alloc.fy14 || alloc.fy15 || alloc.fy16));
    });
    if (!allFieldsValid) return false;
    
    // Check total equals contract value
    return totalAllocation === contractValue;
  }, [allocations, contractValue, totalAllocation]);

  const handleSubmit = () => {
    // Transform allocations to include fiscalYearAmounts from flattened properties
    const coaAllocations = allocations.map((alloc: any) => {
      // Build fiscalYearAmounts from flattened fy12, fy13, etc. properties
      const fiscalYearAmounts: Record<string, number> = {};
      if (alloc.fy12 && alloc.fy12 > 0) fiscalYearAmounts['FY12'] = alloc.fy12;
      if (alloc.fy13 && alloc.fy13 > 0) fiscalYearAmounts['FY13'] = alloc.fy13;
      if (alloc.fy14 && alloc.fy14 > 0) fiscalYearAmounts['FY14'] = alloc.fy14;
      if (alloc.fy15 && alloc.fy15 > 0) fiscalYearAmounts['FY15'] = alloc.fy15;
      if (alloc.fy16 && alloc.fy16 > 0) fiscalYearAmounts['FY16'] = alloc.fy16;
      
      // Calculate total from fiscal years
      const total = Object.values(fiscalYearAmounts).reduce((sum, amt) => sum + (amt || 0), 0);
      
      return {
        uobc: alloc.uobc,
        subOc: alloc.subOc,
        rc: alloc.rc,
        amount: total || alloc.amount,
        fiscalYearAmounts: Object.keys(fiscalYearAmounts).length > 0 ? fiscalYearAmounts : undefined,
      };
    });
    
    const request: ContractConfigurationRequest = {
      contractId,
      coaAllocations,
    };
    configureMutation.mutate(request);
  };

  // Define tabs with lazy loading - MUST be before early returns (Rules of Hooks)
  const contractForTabs = contract;
  const tabs: TabItem[] = useMemo(
    () => [
      {
        id: 'financials',
        label: 'Contract Financials',
        content: (
          <ContractFinancialsTab
            contractId={contractId}
            contractValue={contractValue}
            contractNumber={contract?.contractNumber}
            allocations={allocations}
            fundingSources={fundingSources}
            onAllocationsChange={setAllocations}
            onFundingSourcesChange={setFundingSources}
            onSave={handleSubmit}
            isValid={isValid}
            isSaving={configureMutation.isPending}
            contractStatusId={contract?.statusId}
            hasCofApproval={!!(contract?.cofWorkflowInstanceKey && contract?.statusId && contract.statusId >= 62)}
          />
        ),
        lazy: true,
      },
      {
        id: 'budgets',
        label: 'Contract Budgets',
        content: <ContractConfigBudgetsTab contractId={contractId} />,
        lazy: true,
      },
      {
        id: 'documents',
        label: 'Documents',
        content: <ContractConfigDocumentsTab contractId={contractId} />,
        lazy: true,
      },
      {
        id: 'comments',
        label: 'Comments',
        content: <ContractConfigCommentsTab contractId={contractId} />,
        lazy: true,
      },
      {
        id: 'workflow',
        label: 'Workflow',
        content: (
          <ContractConfigWorkflowTab
            processInstanceKey={
              contractForTabs?.configurationWorkflowInstanceKey
                ? parseInt(contractForTabs.configurationWorkflowInstanceKey)
                : undefined
            }
          />
        ),
        lazy: true,
        badge: contractForTabs?.configurationWorkflowInstanceKey ? '1' : undefined,
      },
    ],
    [
      contractId,
      contractValue,
      contract?.contractNumber,
      allocations,
      fundingSources,
      isValid,
      configureMutation.isPending,
      contractForTabs?.configurationWorkflowInstanceKey,
    ]
  );

  return (
    <div className="max-w-7xl mx-auto space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900 dark:text-white">
            Configure Contract: {contract?.contractNumber || contractId}
          </h1>
          <p className="text-sm text-gray-500 dark:text-gray-400 mt-1">
            {contract?.contractTitle || 'Contract Configuration'}
          </p>
        </div>
        <Button variant="outline" onClick={() => navigate(`/contracts/${contractId}`)}>
          Cancel
        </Button>
      </div>

      {/* Tab-based Content */}
      <Tabs
        items={tabs}
        defaultTab="financials"
        variant="underline"
        className="shadow-lg"
        contentClassName="min-h-[500px]"
      />
    </div>
  );
};
