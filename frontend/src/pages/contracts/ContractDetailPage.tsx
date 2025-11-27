import { useParams, useNavigate } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { Button } from '../../components/atoms/Button';
import { contractService } from '../../services/contractService';
import { useToast } from '../../hooks/useToast';
import { Loader } from '../../components/atoms/Loader';
import { RoleGate } from '../../frameworks/authorization';
import { useAppSelector } from '../../store/hooks';
import { Tabs, type TabItem } from '../../frameworks/ui';
import {
  ContractInfoTab,
  ContractBudgetTab,
  WorkflowDetailsTab,
  DocumentDetailsTab,
} from './components/tabs';
import { useMemo } from 'react';

/**
 * Contract detail page with modern tab-based layout.
 * Framework-based: Uses reusable Tabs component for enterprise UI
 * 
 * Features:
 * - Tab-based navigation (Contract Info, Budget, Workflow, Documents)
 * - Async loading for each tab (lazy loading)
 * - Modern, enterprise-grade UI
 * - Reusable pattern for future screens
 */
export const ContractDetailPage = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const toast = useToast();
  const user = useAppSelector((state) => state.auth.user);
  const contractId = id ? parseInt(id) : 0;

  // Load basic contract data for header
  const { data, isLoading, error } = useQuery({
    queryKey: ['contract', contractId],
    queryFn: () => contractService.getContract(contractId),
    enabled: !!contractId,
  });

  // Define tabs with lazy loading - MUST be before early returns (Rules of Hooks)
  // Use optional chaining for useMemo (contract may be undefined initially)
  const contractForTabs = data?.data?.contract;
  const tabs: TabItem[] = useMemo(
    () => [
      {
        id: 'info',
        label: 'Contract Information',
        content: <ContractInfoTab contractId={contractId} />,
        lazy: true, // Load only when tab is active
      },
      {
        id: 'budget',
        label: 'Budget',
        content: <ContractBudgetTab contractId={contractId} />,
        lazy: true,
      },
      {
        id: 'workflow',
        label: 'Workflow Details',
        content: <WorkflowDetailsTab contractId={contractId} />,
        lazy: true,
        badge: contractForTabs?.configurationWorkflowInstanceKey || contractForTabs?.cofWorkflowInstanceKey ? '1' : undefined,
      },
      {
        id: 'documents',
        label: 'Documents',
        content: <DocumentDetailsTab contractId={contractId} />,
        lazy: true,
      },
    ],
    [contractId, contractForTabs?.configurationWorkflowInstanceKey, contractForTabs?.cofWorkflowInstanceKey]
  );

  // Early returns AFTER all hooks
  if (isLoading) {
    return (
      <div className="flex justify-center items-center h-64">
        <Loader size="lg" />
      </div>
    );
  }

  if (error || !data?.data) {
    toast.error('Failed to load contract');
    return null;
  }

  // After early returns, we know data.data exists
  const contract = data.data.contract;

  return (
    <div className="max-w-7xl mx-auto space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900 dark:text-white">
            Contract: {contract.contractNumber}
          </h1>
          <p className="text-sm text-gray-500 dark:text-gray-400 mt-1">
            {contract.contractTitle}
          </p>
        </div>
        <div className="flex space-x-2">
          {contract.statusId === 59 && (
            <RoleGate permission="CONTRACT_CONFIGURE">
              <Button
                variant="primary"
                onClick={() => navigate(`/contracts/${contractId}/configure`)}
              >
                Configure
              </Button>
            </RoleGate>
          )}
          <Button variant="outline" onClick={() => navigate('/contracts')}>
            Back to List
          </Button>
        </div>
      </div>

      {/* Tab-based Content */}
      <Tabs
        items={tabs}
        defaultTab="info"
        variant="underline"
        className="shadow-lg"
        contentClassName="min-h-[500px]"
      />
    </div>
  );
};




