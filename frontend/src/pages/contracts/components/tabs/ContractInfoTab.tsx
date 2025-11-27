import { useQuery } from '@tanstack/react-query';
import { contractService } from '../../../../services/contractService';
import { Loader } from '../../../../components/atoms/Loader';

interface ContractInfoTabProps {
  contractId: number;
}

/**
 * Contract Information Tab
 * Displays basic contract details and COA allocations
 */
export const ContractInfoTab = ({ contractId }: ContractInfoTabProps) => {
  const { data, isLoading, error } = useQuery({
    queryKey: ['contract', contractId],
    queryFn: () => contractService.getContract(contractId),
    enabled: !!contractId,
  });

  if (isLoading) {
    return (
      <div className="flex justify-center items-center py-12">
        <Loader size="md" />
      </div>
    );
  }

  if (error || !data?.data) {
    return (
      <div className="text-center py-12 text-red-600 dark:text-red-400">
        Failed to load contract information
      </div>
    );
  }

  const contract = data.data.contract;
  const configurations = data.data.configurations || [];

  return (
    <div className="space-y-6">
      {/* Contract Basic Information */}
      <div>
        <h3 className="text-lg font-semibold text-gray-900 dark:text-white mb-4">
          Contract Information
        </h3>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          <div>
            <p className="text-sm text-gray-500 dark:text-gray-400 mb-1">Contract Number</p>
            <p className="text-base font-medium text-gray-900 dark:text-white">
              {contract.contractNumber}
            </p>
          </div>
          <div>
            <p className="text-sm text-gray-500 dark:text-gray-400 mb-1">Status</p>
            <span
              className={`inline-block px-2 py-1 text-xs font-semibold rounded-full ${
                contract.statusId === 59
                  ? 'bg-yellow-100 text-yellow-800 dark:bg-yellow-900/20 dark:text-yellow-300'
                  : contract.statusId === 60
                  ? 'bg-blue-100 text-blue-800 dark:bg-blue-900/20 dark:text-blue-300'
                  : contract.statusId === 62
                  ? 'bg-green-100 text-green-800 dark:bg-green-900/20 dark:text-green-300'
                  : 'bg-gray-100 text-gray-800 dark:bg-gray-700 dark:text-gray-300'
              }`}
            >
              {contract.statusName}
            </span>
          </div>
          <div>
            <p className="text-sm text-gray-500 dark:text-gray-400 mb-1">Contract Value</p>
            <p className="text-base font-medium text-gray-900 dark:text-white">
              ${contract.contractValue.toLocaleString()}
            </p>
          </div>
          <div>
            <p className="text-sm text-gray-500 dark:text-gray-400 mb-1">Contract Amount</p>
            <p className="text-base font-medium text-gray-900 dark:text-white">
              ${contract.contractAmount.toLocaleString()}
            </p>
          </div>
          <div>
            <p className="text-sm text-gray-500 dark:text-gray-400 mb-1">Start Date</p>
            <p className="text-base font-medium text-gray-900 dark:text-white">
              {new Date(contract.contractStartDate).toLocaleDateString()}
            </p>
          </div>
          <div>
            <p className="text-sm text-gray-500 dark:text-gray-400 mb-1">End Date</p>
            <p className="text-base font-medium text-gray-900 dark:text-white">
              {new Date(contract.contractEndDate).toLocaleDateString()}
            </p>
          </div>
          <div>
            <p className="text-sm text-gray-500 dark:text-gray-400 mb-1">Agency ID</p>
            <p className="text-base font-medium text-gray-900 dark:text-white">
              {contract.agencyId}
            </p>
          </div>
          <div>
            <p className="text-sm text-gray-500 dark:text-gray-400 mb-1">Provider ID</p>
            <p className="text-base font-medium text-gray-900 dark:text-white">
              {contract.providerId}
            </p>
          </div>
          <div>
            <p className="text-sm text-gray-500 dark:text-gray-400 mb-1">Program ID</p>
            <p className="text-base font-medium text-gray-900 dark:text-white">
              {contract.programId || 'N/A'}
            </p>
          </div>
        </div>
      </div>

      {/* Chart of Accounts Allocation */}
      {configurations.length > 0 && (
        <div className="border-t border-gray-200 dark:border-gray-700 pt-6">
          <h3 className="text-lg font-semibold text-gray-900 dark:text-white mb-4">
            Chart of Accounts Allocation
          </h3>
          <div className="overflow-x-auto">
            <table className="min-w-full divide-y divide-gray-200 dark:divide-gray-700">
              <thead className="bg-gray-50 dark:bg-gray-700">
                <tr>
                  <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                    UOBC
                  </th>
                  <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                    Sub OC
                  </th>
                  <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                    RC
                  </th>
                  <th className="px-4 py-3 text-right text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                    Amount
                  </th>
                </tr>
              </thead>
              <tbody className="bg-white dark:bg-gray-800 divide-y divide-gray-200 dark:divide-gray-700">
                {configurations.map((config) => (
                  <tr key={config.id} className="hover:bg-gray-50 dark:hover:bg-gray-700">
                    <td className="px-4 py-3 text-sm text-gray-900 dark:text-white">
                      {config.uobc}
                    </td>
                    <td className="px-4 py-3 text-sm text-gray-500 dark:text-gray-300">
                      {config.subOc}
                    </td>
                    <td className="px-4 py-3 text-sm text-gray-500 dark:text-gray-300">
                      {config.rc}
                    </td>
                    <td className="px-4 py-3 text-sm text-gray-500 dark:text-gray-300 text-right">
                      ${config.amount.toLocaleString()}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
          <div className="mt-4 flex justify-end">
            <div className="text-sm">
              <span className="text-gray-500 dark:text-gray-400">Total Allocation: </span>
              <span className="font-semibold text-gray-900 dark:text-white">
                $
                {configurations
                  .reduce((sum, config) => sum + config.amount, 0)
                  .toLocaleString()}
              </span>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

