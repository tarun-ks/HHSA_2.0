import { useState, useMemo } from 'react';
import { useQuery } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';
import { Button } from '../../components/atoms/Button';
import { contractService, Contract } from '../../services/contractService';
import { useToast } from '../../hooks/useToast';
import { Loader } from '../../components/atoms/Loader';
import { RoleGate } from '../../frameworks/authorization';
import { ListPageLayout, type SearchFilter } from '../../frameworks/ui';

/**
 * Contract list page with enterprise search and filter.
 * Framework-based: Uses reusable ListPageLayout and SearchBar components
 * 
 * Features:
 * - Real-time search with debouncing
 * - Advanced filters (Status, Date Range, Value Range)
 * - Pagination
 * - Modern, enterprise UI
 * - Reusable pattern for future list screens
 */
export const ContractListPage = () => {
  const navigate = useNavigate();
  const toast = useToast();
  const [page, setPage] = useState(0);
  const [size] = useState(20);
  const [searchTerm, setSearchTerm] = useState('');
  const [filters, setFilters] = useState<Record<string, any>>({});

  // Build query parameters
  const queryParams = useMemo(() => {
    const params: any = { page, size };
    if (searchTerm) {
      params.search = searchTerm;
    }
    if (filters.status) {
      params.statusId = filters.status;
    }
    if (filters.startDate) {
      params.startDate = filters.startDate;
    }
    if (filters.endDate) {
      params.endDate = filters.endDate;
    }
    if (filters.minValue) {
      params.minValue = filters.minValue;
    }
    if (filters.maxValue) {
      params.maxValue = filters.maxValue;
    }
    return params;
  }, [page, size, searchTerm, filters]);

  const { data, isLoading, error } = useQuery({
    queryKey: ['contracts', queryParams],
    queryFn: () => contractService.getContracts(queryParams),
  });

  const handleCreateContract = () => {
    navigate('/contracts/new');
  };

  const handleViewContract = (id: number) => {
    navigate(`/contracts/${id}`);
  };

  const handleConfigureContract = (id: number) => {
    navigate(`/contracts/${id}/configure`);
  };

  const handleSearch = (term: string, filterValues: Record<string, any>) => {
    setSearchTerm(term);
    setFilters(filterValues);
    setPage(0); // Reset to first page on search
  };

  const handleReset = () => {
    setSearchTerm('');
    setFilters({});
    setPage(0);
  };

  // Define search filters
  const searchFilters: SearchFilter[] = [
    {
      key: 'status',
      label: 'Status',
      type: 'select',
      options: [
        { value: '59', label: 'Pending Configuration' },
        { value: '60', label: 'Pending COF' },
        { value: '61', label: 'Pending Registration' },
        { value: '62', label: 'Registered' },
        { value: '67', label: 'Suspended' },
      ],
    },
    {
      key: 'startDate',
      label: 'Start Date',
      type: 'date',
    },
    {
      key: 'endDate',
      label: 'End Date',
      type: 'date',
    },
    {
      key: 'minValue',
      label: 'Min Value',
      type: 'number',
      placeholder: 'Minimum contract value',
    },
    {
      key: 'maxValue',
      label: 'Max Value',
      type: 'number',
      placeholder: 'Maximum contract value',
    },
  ];

  if (isLoading) {
    return (
      <div className="flex justify-center items-center h-64">
        <Loader size="lg" />
      </div>
    );
  }

  if (error) {
    toast.error('Failed to load contracts');
    return null;
  }

  const contracts = data?.data?.content || [];
  const metadata = data?.data?.metadata;

  return (
    <ListPageLayout
      title="Contracts"
      subtitle="Manage and view all contracts"
      searchPlaceholder="Search by contract number, title, or agency..."
      searchFilters={searchFilters}
      onSearch={handleSearch}
      onReset={handleReset}
      actions={
        <RoleGate permission="CONTRACT_CREATE">
          <Button onClick={handleCreateContract} variant="primary">
            Create Contract
          </Button>
        </RoleGate>
      }
    >

      {contracts.length === 0 ? (
        <div className="bg-white dark:bg-gray-800 shadow rounded-lg p-12 text-center">
          <p className="text-gray-500 dark:text-gray-400 mb-4">
            {searchTerm || Object.keys(filters).some((key) => filters[key])
              ? 'No contracts match your search criteria'
              : 'No contracts found'}
          </p>
          {!searchTerm && !Object.keys(filters).some((key) => filters[key]) && (
            <RoleGate permission="CONTRACT_CREATE">
              <Button onClick={handleCreateContract} variant="primary" className="mt-4">
                Create Your First Contract
              </Button>
            </RoleGate>
          )}
        </div>
      ) : (
        <>
          <div className="bg-white dark:bg-gray-800 shadow rounded-lg overflow-hidden">
            <table className="min-w-full divide-y divide-gray-200 dark:divide-gray-700">
              <thead className="bg-gray-50 dark:bg-gray-700">
                <tr>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                    Contract #
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                    Title
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                    Value
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                    Status
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                    Start Date
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                    End Date
                  </th>
                  <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                    Actions
                  </th>
                </tr>
              </thead>
              <tbody className="bg-white dark:bg-gray-800 divide-y divide-gray-200 dark:divide-gray-700">
                {contracts.map((contract: Contract) => (
                  <tr key={contract.id} className="hover:bg-gray-50 dark:hover:bg-gray-700">
                    <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900 dark:text-white">
                      {contract.contractNumber}
                    </td>
                    <td className="px-6 py-4 text-sm text-gray-500 dark:text-gray-300">
                      {contract.contractTitle}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500 dark:text-gray-300">
                      ${contract.contractValue.toLocaleString()}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <span
                        className={`px-2 py-1 text-xs font-semibold rounded-full ${
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
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500 dark:text-gray-300">
                      {new Date(contract.contractStartDate).toLocaleDateString()}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500 dark:text-gray-300">
                      {new Date(contract.contractEndDate).toLocaleDateString()}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium space-x-2">
                      <Button
                        variant="ghost"
                        size="sm"
                        onClick={() => handleViewContract(contract.id)}
                      >
                        View
                      </Button>
                      {contract.statusId === 59 && (
                        <RoleGate permission="CONTRACT_CONFIGURE">
                          <Button
                            variant="outline"
                            size="sm"
                            onClick={() => handleConfigureContract(contract.id)}
                          >
                            Configure
                          </Button>
                        </RoleGate>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          {/* Pagination */}
          {metadata && (
            <div className="flex items-center justify-between">
              <div className="text-sm text-gray-700 dark:text-gray-300">
                Showing {page * size + 1} to {Math.min((page + 1) * size, metadata.totalElements)} of{' '}
                {metadata.totalElements} contracts
              </div>
              <div className="flex space-x-2">
                <Button
                  variant="outline"
                  size="sm"
                  disabled={metadata.first}
                  onClick={() => setPage(0)}
                >
                  First
                </Button>
                <Button
                  variant="outline"
                  size="sm"
                  disabled={metadata.first}
                  onClick={() => setPage(page - 1)}
                >
                  Previous
                </Button>
                <Button
                  variant="outline"
                  size="sm"
                  disabled={metadata.last}
                  onClick={() => setPage(page + 1)}
                >
                  Next
                </Button>
                <Button
                  variant="outline"
                  size="sm"
                  disabled={metadata.last}
                  onClick={() => setPage(metadata.totalPages - 1)}
                >
                  Last
                </Button>
              </div>
            </div>
          )}
        </>
      )}
    </ListPageLayout>
  );
};




