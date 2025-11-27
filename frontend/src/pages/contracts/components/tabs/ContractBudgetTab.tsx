import { useQuery } from '@tanstack/react-query';
import { contractService } from '../../../../services/contractService';
import { Loader } from '../../../../components/atoms/Loader';
import { useNavigate } from 'react-router-dom';
import { Button } from '../../../../components/atoms/Button';

interface ContractBudgetTabProps {
  contractId: number;
}

/**
 * Contract Budget Tab
 * Displays budget information and allows navigation to budget setup
 */
export const ContractBudgetTab = ({ contractId }: ContractBudgetTabProps) => {
  const navigate = useNavigate();

  const { data, isLoading, error } = useQuery({
    queryKey: ['contract-budgets', contractId],
    queryFn: () => contractService.getContractBudgets(contractId),
    enabled: !!contractId,
  });

  if (isLoading) {
    return (
      <div className="flex justify-center items-center py-12">
        <Loader size="md" />
      </div>
    );
  }

  if (error) {
    return (
      <div className="text-center py-12 text-red-600 dark:text-red-400">
        Failed to load budget information
      </div>
    );
  }

  const budgets = data?.data || [];

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h3 className="text-lg font-semibold text-gray-900 dark:text-white">
          Contract Budgets
        </h3>
        <Button
          variant="primary"
          onClick={() => navigate(`/contracts/${contractId}/budget`)}
        >
          Setup Budget
        </Button>
      </div>

      {budgets.length === 0 ? (
        <div className="text-center py-12 border border-gray-200 dark:border-gray-700 rounded-lg">
          <p className="text-gray-500 dark:text-gray-400 mb-4">
            No budget allocations configured yet
          </p>
          <Button
            variant="outline"
            onClick={() => navigate(`/contracts/${contractId}/budget`)}
          >
            Create Budget
          </Button>
        </div>
      ) : (
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200 dark:divide-gray-700">
            <thead className="bg-gray-50 dark:bg-gray-700">
              <tr>
                <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                  Fiscal Year
                </th>
                <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                  Period
                </th>
                <th className="px-4 py-3 text-right text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                  Amount
                </th>
              </tr>
            </thead>
            <tbody className="bg-white dark:bg-gray-800 divide-y divide-gray-200 dark:divide-gray-700">
              {budgets.map((budget: any) => (
                <tr key={budget.id} className="hover:bg-gray-50 dark:hover:bg-gray-700">
                  <td className="px-4 py-3 text-sm text-gray-900 dark:text-white">
                    {budget.fiscalYear || 'N/A'}
                  </td>
                  <td className="px-4 py-3 text-sm text-gray-500 dark:text-gray-300">
                    {budget.period || 'N/A'}
                  </td>
                  <td className="px-4 py-3 text-sm text-gray-500 dark:text-gray-300 text-right">
                    ${budget.amount?.toLocaleString() || '0'}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
};

