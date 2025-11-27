import { useState, useCallback, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import { EditableTable, EditableTableColumn } from '../../../../components/organisms/EditableTable';
import { Button } from '../../../../components/atoms/Button';
import { FundingSourceAllocationTable } from '../FundingSourceAllocationTable';
import { 
  CONTRACT_CONFIGURATION_ROW_RULES,
  CONTRACT_CONFIGURATION_FORM_RULES,
  type COAAllocation 
} from '../../../../frameworks/business-rules/rules/contract/contractConfigurationRules';
import { useValidation, createRuleContext } from '../../../../frameworks/business-rules';
import { ExcelExportPlugin, PrintPlugin } from '../../../../frameworks/grid';
import { PermissionGate } from '../../../../frameworks/authorization';

interface ContractFinancialsTabProps {
  contractId: number;
  contractValue: number;
  contractNumber?: string;
  allocations: COAAllocation[];
  fundingSources: any[];
  onAllocationsChange: (allocations: COAAllocation[]) => void;
  onFundingSourcesChange: (fundingSources: any[]) => void;
  onSave: () => void;
  isValid: boolean;
  isSaving: boolean;
  contractStatusId?: number; // For determining readonly state (after COF approval)
  hasCofApproval?: boolean; // Whether contract has received Certificate of Funds
}

/**
 * Contract Financials Tab
 * Displays COA allocations and funding sources
 */
export const ContractFinancialsTab = ({
  contractId,
  contractValue,
  contractNumber,
  allocations,
  fundingSources,
  onAllocationsChange,
  onFundingSourcesChange,
  onSave,
  isValid,
  isSaving,
  contractStatusId,
  hasCofApproval = false,
}: ContractFinancialsTabProps) => {
  const navigate = useNavigate();
  const [showFundingSources, setShowFundingSources] = useState(false);

  // Determine if contract is readonly (after COF approval - status 62+)
  // Based on PDF: "Once the task has been approved, the Agency users will not be able to remove the tabs"
  // Status 62 = Registered (after COF approval)
  const isReadOnly = hasCofApproval || (contractStatusId !== undefined && contractStatusId >= 62);

  // Business rules validation
  const ruleContext = useMemo(
    () => createRuleContext({ contractValue, allRows: allocations }),
    [contractValue, allocations]
  );
  
  // Row-level validation
  const rowValidation = useValidation<COAAllocation>(
    CONTRACT_CONFIGURATION_ROW_RULES,
    ruleContext
  );
  
  // Form-level validation (kept for future use)
  // const formValidation = useValidation<COAAllocation>(
  //   CONTRACT_CONFIGURATION_FORM_RULES,
  //   ruleContext
  // );

  // Handle allocation changes and sync amounts with fiscal year totals
  const handleAllocationChange = useCallback((newAllocations: COAAllocation[]) => {
    // Calculate amounts from fiscal year totals
    const updated = newAllocations.map((alloc: any) => {
      const fiscalYearTotal = (alloc.fy12 || 0) + (alloc.fy13 || 0) + (alloc.fy14 || 0) + 
                               (alloc.fy15 || 0) + (alloc.fy16 || 0);
      return {
        ...alloc,
        amount: fiscalYearTotal || alloc.amount || 0,
        fiscalYearAmounts: {
          FY12: alloc.fy12 || 0,
          FY13: alloc.fy13 || 0,
          FY14: alloc.fy14 || 0,
          FY15: alloc.fy15 || 0,
          FY16: alloc.fy16 || 0,
        },
      };
    });
    onAllocationsChange(updated);
  }, [onAllocationsChange]);

  // Calculate total allocation
  const totalAllocation = allocations.reduce((sum, alloc: any) => {
    const fiscalYearTotal = (alloc.fy12 || 0) + (alloc.fy13 || 0) + (alloc.fy14 || 0) + 
                            (alloc.fy15 || 0) + (alloc.fy16 || 0);
    return sum + (fiscalYearTotal || alloc.amount || 0);
  }, 0);

  // Create new empty row
  const createNewRow = (): COAAllocation & { fy12?: number; fy13?: number; fy14?: number; fy15?: number; fy16?: number } => ({
    uobc: '',
    subOc: '',
    rc: '',
    amount: 0,
    fiscalYearAmounts: {
      FY12: 0,
      FY13: 0,
      FY14: 0,
      FY15: 0,
      FY16: 0,
    },
    fy12: 0,
    fy13: 0,
    fy14: 0,
    fy15: 0,
    fy16: 0,
  });

  // Get validation function for EditableTable
  const validateRow = rowValidation.getValidationFunction();

  // Define columns for editable table
  const columns: EditableTableColumn<COAAllocation>[] = [
    {
      key: 'uobc',
      label: 'UOBC (Unit of Appropriation)',
      required: true,
      width: '200px',
      validate: (value) => {
        if (!value || String(value).trim() === '') {
          return 'UOBC is required';
        }
        return undefined;
      },
      renderEdit: (row, _index, onChange) => (
        <input
          type="text"
          value={row.uobc || ''}
          onChange={(e) => onChange(e.target.value)}
          className="w-full px-2 py-1 border rounded dark:bg-gray-700 dark:text-white"
          placeholder="UOBC"
        />
      ),
    },
    {
      key: 'subOc',
      label: 'Sub Object Code',
      required: true,
      width: '200px',
      validate: (value) => {
        if (!value || String(value).trim() === '') {
          return 'Sub Object Code is required';
        }
        return undefined;
      },
      renderEdit: (row, _index, onChange) => (
        <input
          type="text"
          value={row.subOc || ''}
          onChange={(e) => onChange(e.target.value)}
          className="w-full px-2 py-1 border rounded dark:bg-gray-700 dark:text-white"
          placeholder="Sub OC"
        />
      ),
    },
    {
      key: 'rc',
      label: 'RC (Responsibility Center)',
      required: true,
      width: '200px',
      validate: (value) => {
        if (!value || String(value).trim() === '') {
          return 'RC is required';
        }
        return undefined;
      },
      renderEdit: (row, _index, onChange) => (
        <input
          type="text"
          value={row.rc || ''}
          onChange={(e) => onChange(e.target.value)}
          className="w-full px-2 py-1 border rounded dark:bg-gray-700 dark:text-white"
          placeholder="RC"
        />
      ),
    },
    {
      key: 'amount',
      label: 'Total',
      required: true,
      width: '150px',
      validate: (value) => {
        const numValue = typeof value === 'number' ? value : parseFloat(String(value));
        if (isNaN(numValue) || numValue <= 0) {
          return 'Total must be greater than 0';
        }
        return undefined;
      },
      render: (row: any) => {
        const total = (row.fy12 || 0) + (row.fy13 || 0) + (row.fy14 || 0) + 
                      (row.fy15 || 0) + (row.fy16 || 0) || row.amount;
        return `$${total.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`;
      },
      renderEdit: (row: any, _index, onChange) => {
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
    // Fiscal Year columns
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

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <p className="text-sm text-gray-500 dark:text-gray-400">Contract Value</p>
          <p className="text-2xl font-bold text-gray-900 dark:text-white">
            ${contractValue.toLocaleString()}
          </p>
        </div>
        <div className="text-right">
          <p className="text-sm text-gray-500 dark:text-gray-400">Total Allocation</p>
          <p
            className={`text-2xl font-bold ${
              totalAllocation === contractValue
                ? 'text-green-600 dark:text-green-400'
                : 'text-red-600 dark:text-red-400'
            }`}
          >
            ${totalAllocation.toLocaleString()}
          </p>
          {totalAllocation !== contractValue && (
            <p className="text-xs text-red-600 dark:text-red-400 mt-1">
              Must equal contract value
            </p>
          )}
        </div>
      </div>

      <div className="border-t border-gray-200 dark:border-gray-700 pt-6">
        <h2 className="text-lg font-semibold text-gray-900 dark:text-white mb-4">
          Chart of Accounts Allocation
        </h2>

        {/* Apply readonly mode if contract is approved (after COF) */}
        <PermissionGate 
          permission="CONTRACT_CONFIGURE" 
          mode={isReadOnly ? "readonly" : undefined}
        >
          <EditableTable
            columns={columns}
            data={allocations}
            onChange={handleAllocationChange}
            createNewRow={createNewRow}
            validateRow={validateRow}
            readOnly={isReadOnly}
            plugins={[
              ExcelExportPlugin({ filename: `contract-${contractId}-coa`, format: 'csv' }) as any,
              PrintPlugin({ title: `Contract ${contractNumber || contractId} - COA Allocations` }) as any,
            ]}
            emptyMessage="No COA allocations. Click 'Add Row' to start."
          />
        </PermissionGate>
      </div>

      {/* Funding Source Allocation Section (Optional) */}
      <div className="border-t border-gray-200 dark:border-gray-700 pt-6">
        <div className="flex items-center justify-between mb-4">
          <h2 className="text-lg font-semibold text-gray-900 dark:text-white">
            Funding Source Allocation (Optional)
          </h2>
          <Button
            variant="outline"
            size="sm"
            onClick={() => setShowFundingSources(!showFundingSources)}
          >
            {showFundingSources ? 'Hide' : 'Show'} Funding Sources
          </Button>
        </div>

        {showFundingSources && (
          <FundingSourceAllocationTable
            contractId={contractId}
            contractValue={contractValue}
            fundingSources={fundingSources}
            onChange={onFundingSourcesChange}
          />
        )}
      </div>

      <div className="flex justify-end space-x-4 border-t border-gray-200 dark:border-gray-700 pt-6">
        <Button
          variant="outline"
          onClick={() => navigate(`/contracts/${contractId}`)}
          disabled={isSaving}
        >
          Cancel
        </Button>
        <div className="flex flex-col items-end">
          {/* Disable save button if readonly (after COF approval) */}
          <Button
            variant="primary"
            onClick={onSave}
            disabled={!isValid || isSaving || isReadOnly}
            isLoading={isSaving}
          >
            Save Configuration
          </Button>
          {!isValid && allocations.length > 0 && (
            <p className="text-xs text-red-600 dark:text-red-400 mt-1">
              Please fix validation errors before saving
            </p>
          )}
          {isReadOnly && (
            <p className="text-xs text-gray-500 dark:text-gray-400 mt-1">
              Configuration is readonly after COF approval
            </p>
          )}
        </div>
      </div>
    </div>
  );
};

