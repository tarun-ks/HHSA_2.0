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
      },
      {
        id: 'documents',
        label: 'Documents',
        content: <DocumentDetailsTab contractId={contractId} />,
        lazy: true,
      },
    ],
    [contractId]
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
    <div className="container-page section-spacing">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div className="flex-1 min-w-0">
          <h1 className="text-responsive-xl font-bold text-gray-900 dark:text-white truncate">
            Contract: {contract.contractNumber}
          </h1>
          <p className="text-sm text-gray-500 dark:text-gray-400 mt-1 truncate-2">
            {contract.contractTitle}
          </p>
        </div>
        <div className="flex flex-col sm:flex-row gap-2 sm:flex-shrink-0">
          {contract.statusId === 59 && (
            <RoleGate permission="CONTRACT_CONFIGURE">
              <Button
                variant="primary"
                onClick={() => navigate(`/contracts/${contractId}/configure`)}
                className="w-full sm:w-auto"
              >
                Configure
              </Button>
            </RoleGate>
          )}
          <Button 
            variant="outline" 
            onClick={() => navigate('/contracts')}
            className="w-full sm:w-auto"
          >
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
        contentClassName="min-h-[400px] sm:min-h-[500px]"
      />
    </div>
  );
};




