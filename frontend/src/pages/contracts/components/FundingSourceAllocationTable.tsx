import { useState, useCallback, useMemo } from 'react';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { EditableTable, EditableTableColumn } from '../../../components/organisms/EditableTable';
import { contractService, ContractFundingSourceRequest } from '../../../services/contractService';
import { useToast } from '../../../hooks/useToast';
import { ExcelExportPlugin, PrintPlugin } from '../../../frameworks/grid';

interface FundingSourceRow {
  fundingSourceId: string;
  amount: number;
  fiscalYearAmounts?: Record<string, number>;
  fy12?: number;
  fy13?: number;
  fy14?: number;
  fy15?: number;
  fy16?: number;
}

interface FundingSourceAllocationTableProps {
  contractId: number;
  contractValue: number;
  fundingSources: FundingSourceRow[];
  onChange: (sources: FundingSourceRow[]) => void;
}

const FUNDING_SOURCE_OPTIONS = ['Federal', 'State', 'City', 'Other'];

/**
 * Funding Source Allocation Table Component
 * Allows users to allocate contract amounts across different funding sources with fiscal year breakdown
 */
export const FundingSourceAllocationTable = ({
  contractId,
  contractValue,
  fundingSources,
  onChange,
}: FundingSourceAllocationTableProps) => {
  const toast = useToast();
  const queryClient = useQueryClient();

  const saveMutation = useMutation({
    mutationFn: (data: ContractFundingSourceRequest) =>
      contractService.saveContractFundingSources(contractId, data),
    onSuccess: (response) => {
      if (response.success) {
        toast.success('Funding sources saved successfully!');
        queryClient.invalidateQueries({ queryKey: ['contract-funding-sources', contractId] });
      } else {
        toast.error(response.error?.description || 'Failed to save funding sources');
      }
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.error?.description || 'An error occurred');
    },
  });

  // Handle funding source changes and sync amounts with fiscal year totals
  const handleFundingSourceChange = useCallback((newSources: FundingSourceRow[]) => {
    // Calculate amounts from fiscal year totals
    const updated = newSources.map((fs) => {
      const fiscalYearTotal = (fs.fy12 || 0) + (fs.fy13 || 0) + (fs.fy14 || 0) + 
                               (fs.fy15 || 0) + (fs.fy16 || 0);
      return {
        ...fs,
        amount: fiscalYearTotal || fs.amount || 0,
        fiscalYearAmounts: {
          FY12: fs.fy12 || 0,
          FY13: fs.fy13 || 0,
          FY14: fs.fy14 || 0,
          FY15: fs.fy15 || 0,
          FY16: fs.fy16 || 0,
        },
      };
    });
    onChange(updated);
  }, [onChange]);

  // Create new empty row
  const createNewRow = (): FundingSourceRow => ({
    fundingSourceId: '',
    amount: 0,
    fiscalYearAmounts: {},
    fy12: 0,
    fy13: 0,
    fy14: 0,
    fy15: 0,
    fy16: 0,
  });

  // Calculate total funding source allocation
  const totalFundingAllocation = fundingSources.reduce((sum, fs: any) => {
    const fiscalYearTotal = (fs.fy12 || 0) + (fs.fy13 || 0) + (fs.fy14 || 0) + 
                            (fs.fy15 || 0) + (fs.fy16 || 0);
    return sum + (fiscalYearTotal || fs.amount || 0);
  }, 0);

  // Define columns for editable table
  const columns: EditableTableColumn<FundingSourceRow>[] = [
    {
      key: 'fundingSourceId',
      label: 'Funding Source',
      required: true,
      width: '200px',
      validate: (value) => {
        if (!value || String(value).trim() === '') {
          return 'Funding source is required';
        }
        return undefined;
      },
      renderEdit: (row, _index, onChange) => (
        <select
          value={row.fundingSourceId || ''}
          onChange={(e) => onChange(e.target.value)}
          className="w-full px-2 py-1 border rounded dark:bg-gray-700 dark:text-white"
        >
          <option value="">Select funding source</option>
          {FUNDING_SOURCE_OPTIONS.map((option) => (
            <option key={option} value={option}>
              {option}
            </option>
          ))}
        </select>
      ),
    },
    {
      key: 'amount',
      label: 'Total',
      required: true,
      width: '150px',
      render: (row: any) => {
        const total = (row.fy12 || 0) + (row.fy13 || 0) + (row.fy14 || 0) + 
                      (row.fy15 || 0) + (row.fy16 || 0) || row.amount;
        return `$${total.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`;
      },
      renderEdit: (row: any, _index, _onChange) => {
        const total = (row.fy12 || 0) + (row.fy13 || 0) + (row.fy14 || 0) + 
                      (row.fy15 || 0) + (row.fy16 || 0) || row.amount;
        return (
          <input
            type="number"
            step="0.01"
            min="0.01"
            value={total || ''}
            readOnly
            className="w-full px-2 py-1 border rounded bg-gray-100 dark:bg-gray-700 dark:text-white"
            placeholder="0.00"
          />
        );
      },
    },
    // Fiscal Year columns (same pattern as COA)
    {
      key: 'fy12',
      label: 'FY12',
      width: '120px',
      render: (row: any) => {
        const amount = row.fy12 || row.fiscalYearAmounts?.['FY12'] || 0;
        return `$${amount.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`;
      },
      renderEdit: (row: any, _index, onChange) => (
        <input
          type="number"
          step="0.01"
          min="0"
          value={row.fy12 || ''}
          onChange={(e) => {
            const newValue = parseFloat(e.target.value) || 0;
            onChange(newValue);
          }}
          className="w-full px-2 py-1 border rounded dark:bg-gray-700 dark:text-white"
          placeholder="0.00"
        />
      ),
    },
    {
      key: 'fy13',
      label: 'FY13',
      width: '120px',
      render: (row: any) => {
        const amount = row.fy13 || row.fiscalYearAmounts?.['FY13'] || 0;
        return `$${amount.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`;
      },
      renderEdit: (row: any, _index, onChange) => (
        <input
          type="number"
          step="0.01"
          min="0"
          value={row.fy13 || ''}
          onChange={(e) => {
            const newValue = parseFloat(e.target.value) || 0;
            onChange(newValue);
          }}
          className="w-full px-2 py-1 border rounded dark:bg-gray-700 dark:text-white"
          placeholder="0.00"
        />
      ),
    },
    {
      key: 'fy14',
      label: 'FY14',
      width: '120px',
      render: (row: any) => {
        const amount = row.fy14 || row.fiscalYearAmounts?.['FY14'] || 0;
        return `$${amount.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`;
      },
      renderEdit: (row: any, _index, onChange) => (
        <input
          type="number"
          step="0.01"
          min="0"
          value={row.fy14 || ''}
          onChange={(e) => {
            const newValue = parseFloat(e.target.value) || 0;
            onChange(newValue);
          }}
          className="w-full px-2 py-1 border rounded dark:bg-gray-700 dark:text-white"
          placeholder="0.00"
        />
      ),
    },
    {
      key: 'fy15',
      label: 'FY15',
      width: '120px',
      render: (row: any) => {
        const amount = row.fy15 || row.fiscalYearAmounts?.['FY15'] || 0;
        return `$${amount.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`;
      },
      renderEdit: (row: any, _index, onChange) => (
        <input
          type="number"
          step="0.01"
          min="0"
          value={row.fy15 || ''}
          onChange={(e) => {
            const newValue = parseFloat(e.target.value) || 0;
            onChange(newValue);
          }}
          className="w-full px-2 py-1 border rounded dark:bg-gray-700 dark:text-white"
          placeholder="0.00"
        />
      ),
    },
    {
      key: 'fy16',
      label: 'FY16',
      width: '120px',
      render: (row: any) => {
        const amount = row.fy16 || row.fiscalYearAmounts?.['FY16'] || 0;
        return `$${amount.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`;
      },
      renderEdit: (row: any, _index, onChange) => (
        <input
          type="number"
          step="0.01"
          min="0"
          value={row.fy16 || ''}
          onChange={(e) => {
            const newValue = parseFloat(e.target.value) || 0;
            onChange(newValue);
          }}
          className="w-full px-2 py-1 border rounded dark:bg-gray-700 dark:text-white"
          placeholder="0.00"
        />
      ),
    },
  ];

  const handleSave = () => {
    // Transform funding sources to request format
    const fundingSourceAllocations = fundingSources.map((fs: any) => {
      const fiscalYearAmounts: Record<string, number> = {};
      if (fs.fy12 && fs.fy12 > 0) fiscalYearAmounts['FY12'] = fs.fy12;
      if (fs.fy13 && fs.fy13 > 0) fiscalYearAmounts['FY13'] = fs.fy13;
      if (fs.fy14 && fs.fy14 > 0) fiscalYearAmounts['FY14'] = fs.fy14;
      if (fs.fy15 && fs.fy15 > 0) fiscalYearAmounts['FY15'] = fs.fy15;
      if (fs.fy16 && fs.fy16 > 0) fiscalYearAmounts['FY16'] = fs.fy16;
      
      const total = Object.values(fiscalYearAmounts).reduce((sum, amt) => sum + (amt || 0), 0);
      
      return {
        fundingSourceId: fs.fundingSourceId,
        amount: total || fs.amount,
        fiscalYearAmounts: Object.keys(fiscalYearAmounts).length > 0 ? fiscalYearAmounts : undefined,
      };
    });
    
    const request: ContractFundingSourceRequest = {
      contractId,
      fundingSourceAllocations,
    };
    saveMutation.mutate(request);
  };

  return (
    <div className="space-y-4">
      <EditableTable
        columns={columns}
        data={fundingSources}
        onChange={handleFundingSourceChange}
        createNewRow={createNewRow}
        plugins={[
          ExcelExportPlugin({ filename: `contract-${contractId}-funding-sources`, format: 'csv' }) as any,
          PrintPlugin({ title: `Contract ${contractId} - Funding Source Allocations` }) as any,
        ]}
        emptyMessage="No funding source allocations. Click 'Add Row' to start."
      />

      {/* Total Funding Allocation Summary */}
      <div className="flex justify-end items-center space-x-4 pt-4 border-t border-gray-200 dark:border-gray-700">
        <div className="text-right">
          <p className="text-sm text-gray-500 dark:text-gray-400">Total Funding Allocation</p>
          <p className="text-xl font-bold text-gray-900 dark:text-white">
            ${totalFundingAllocation.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
          </p>
        </div>
        <button
          onClick={handleSave}
          disabled={saveMutation.isPending || fundingSources.length === 0}
          className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed"
        >
          {saveMutation.isPending ? 'Saving...' : 'Save Funding Sources'}
        </button>
      </div>
    </div>
  );
};

