import { useState, useEffect, useMemo } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { Button } from '../../components/atoms/Button';
import { EditableTable, EditableTableColumn } from '../../components/organisms/EditableTable';
import { contractService, ContractBudgetRequest, BudgetTemplate, ContractBudgetTemplateRequest } from '../../services/contractService';
import { useToast } from '../../hooks/useToast';
import { Loader } from '../../components/atoms/Loader';
import { 
  CONTRACT_BUDGET_RULES,
  type BudgetAllocation 
} from '../../frameworks/business-rules/rules/contract/contractBudgetRules';
import { useValidation, createRuleContext } from '../../frameworks/business-rules';
import { ExcelExportPlugin, PrintPlugin } from '../../frameworks/grid';
import { PermissionGate } from '../../frameworks/authorization';

// Use BudgetAllocation type from business rules
type BudgetRow = BudgetAllocation;

/**
 * Contract budget setup page.
 * Allows users to configure fiscal year budget allocations using an editable grid.
 */
export const ContractBudgetPage = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const toast = useToast();
  const queryClient = useQueryClient();
  const contractId = id ? parseInt(id) : 0;

  const [budgetRows, setBudgetRows] = useState<BudgetRow[]>([]);
  const [selectedTemplates, setSelectedTemplates] = useState<number[]>([]);

  // Business rules validation
  const ruleContext = useMemo(
    () => createRuleContext({ allRows: budgetRows }),
    [budgetRows]
  );
  
  const { getValidationFunction } = useValidation<BudgetRow>(
    CONTRACT_BUDGET_RULES,
    ruleContext
  );

  // Get validation function for EditableTable
  const validateRow = getValidationFunction();

  const { data: contractData, isLoading: isLoadingContract } = useQuery({
    queryKey: ['contract', contractId],
    queryFn: () => contractService.getContract(contractId),
    enabled: !!contractId,
  });

  const { data: budgetsData, isLoading: isLoadingBudgets } = useQuery({
    queryKey: ['contract-budgets', contractId],
    queryFn: () => contractService.getContractBudgets(contractId),
    enabled: !!contractId,
  });

  const { data: allTemplatesData } = useQuery({
    queryKey: ['budget-templates'],
    queryFn: () => contractService.getAllBudgetTemplates(),
  });

  const { data: contractTemplatesData } = useQuery({
    queryKey: ['contract-budget-templates', contractId],
    queryFn: () => contractService.getContractBudgetTemplates(contractId),
    enabled: !!contractId,
  });

  const saveMutation = useMutation({
    mutationFn: (data: ContractBudgetRequest) =>
      contractService.saveContractBudgets(contractId, data),
    onSuccess: (response) => {
      if (response.success) {
        toast.success('Budget allocations saved successfully!');
        queryClient.invalidateQueries({ queryKey: ['contract-budgets', contractId] });
        queryClient.invalidateQueries({ queryKey: ['contract', contractId] });
      } else {
        toast.error(response.error?.description || 'Failed to save budget allocations');
      }
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.error?.description || 'An error occurred');
    },
  });

  // Load existing budgets into editable grid
  useEffect(() => {
    if (budgetsData?.data && budgetsData.data.length > 0) {
      setBudgetRows(
        budgetsData.data.map((budget) => ({
          fiscalYear: budget.fiscalYear,
          budgetCode: budget.budgetCode,
          objectCode: budget.objectCode,
          amount: budget.amount,
        }))
      );
    }
  }, [budgetsData]);

  // Load selected budget templates
  useEffect(() => {
    if (contractTemplatesData?.data) {
      setSelectedTemplates(contractTemplatesData.data.map((t) => t.id));
    }
  }, [contractTemplatesData]);

  const saveTemplatesMutation = useMutation({
    mutationFn: (data: ContractBudgetTemplateRequest) =>
      contractService.saveContractBudgetTemplates(contractId, data),
    onSuccess: (response) => {
      if (response.success) {
        toast.success('Budget templates saved successfully!');
        queryClient.invalidateQueries({ queryKey: ['contract-budget-templates', contractId] });
      } else {
        toast.error(response.error?.description || 'Failed to save budget templates');
      }
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.error?.description || 'An error occurred');
    },
  });

  const contract = contractData?.data?.contract;
  
  // Determine if contract is readonly (after COF approval - status 62+)
  // Based on PDF: "budget categories cannot be removed once added"
  const isReadOnly = contract?.statusId !== undefined && contract.statusId >= 62;

  // Define columns for editable table
  const columns: EditableTableColumn<BudgetRow>[] = [
    {
      key: 'fiscalYear',
      label: 'Fiscal Year',
      required: true,
      width: '150px',
      validate: (value) => {
        if (!value || String(value).trim() === '') {
          return 'Fiscal year is required';
        }
        if (String(value).length > 10) {
          return 'Fiscal year must not exceed 10 characters';
        }
        return undefined;
      },
      renderEdit: (row, _index, onChange) => (
        <input
          type="text"
          value={row.fiscalYear || ''}
          onChange={(e) => onChange(e.target.value)}
          className="w-full px-2 py-1 border rounded dark:bg-gray-700 dark:text-white"
          placeholder="YYYY"
        />
      ),
    },
    {
      key: 'budgetCode',
      label: 'Budget Code',
      required: true,
      width: '200px',
      validate: (value) => {
        if (!value || String(value).trim() === '') {
          return 'Budget code is required';
        }
        if (String(value).length > 50) {
          return 'Budget code must not exceed 50 characters';
        }
        return undefined;
      },
      renderEdit: (row, _index, onChange) => (
        <input
          type="text"
          value={row.budgetCode || ''}
          onChange={(e) => onChange(e.target.value)}
          className="w-full px-2 py-1 border rounded dark:bg-gray-700 dark:text-white"
          placeholder="Budget code"
        />
      ),
    },
    {
      key: 'objectCode',
      label: 'Object Code',
      required: true,
      width: '200px',
      validate: (value) => {
        if (!value || String(value).trim() === '') {
          return 'Object code is required';
        }
        if (String(value).length > 50) {
          return 'Object code must not exceed 50 characters';
        }
        return undefined;
      },
      renderEdit: (row, _index, onChange) => (
        <input
          type="text"
          value={row.objectCode || ''}
          onChange={(e) => onChange(e.target.value)}
          className="w-full px-2 py-1 border rounded dark:bg-gray-700 dark:text-white"
          placeholder="Object code"
        />
      ),
    },
    {
      key: 'amount',
      label: 'Amount',
      required: true,
      width: '200px',
      validate: (value) => {
        const numValue = typeof value === 'number' ? value : parseFloat(String(value));
        if (isNaN(numValue) || numValue <= 0) {
          return 'Amount must be greater than 0';
        }
        return undefined;
      },
      render: (row) => `$${row.amount.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`,
      renderEdit: (row, _index, onChange) => (
        <input
          type="number"
          step="0.01"
          min="0.01"
          value={row.amount || ''}
          onChange={(e) => onChange(parseFloat(e.target.value) || 0)}
          className="w-full px-2 py-1 border rounded dark:bg-gray-700 dark:text-white"
          placeholder="0.00"
        />
      ),
    },
  ];

  const createNewRow = (): BudgetRow => ({
    fiscalYear: '',
    budgetCode: '',
    objectCode: '',
    amount: 0,
  });

  const handleSave = () => {
    // Validate all rows have required fields
    const invalidRows = budgetRows.filter(
      (row) =>
        !row.fiscalYear?.trim() ||
        !row.budgetCode?.trim() ||
        !row.objectCode?.trim() ||
        !row.amount ||
        row.amount <= 0
    );

    if (invalidRows.length > 0) {
      toast.error('Please fill in all required fields for all budget allocations');
      return;
    }

    const request: ContractBudgetRequest = {
      contractId,
      allocations: budgetRows.map((row) => ({
        fiscalYear: row.fiscalYear.trim(),
        budgetCode: row.budgetCode.trim(),
        objectCode: row.objectCode.trim(),
        amount: row.amount,
      })),
    };

    saveMutation.mutate(request);
  };

  // Calculate totals by fiscal year
  const totalsByFiscalYear = budgetRows.reduce((acc, row) => {
    const fy = row.fiscalYear || 'Unknown';
    acc[fy] = (acc[fy] || 0) + (row.amount || 0);
    return acc;
  }, {} as Record<string, number>);

  if (isLoadingContract || isLoadingBudgets) {
    return (
      <div className="flex justify-center items-center h-64">
        <Loader size="lg" />
      </div>
    );
  }

  if (!contract) {
    return <div>Contract not found</div>;
  }

  return (
    <div className="max-w-6xl mx-auto space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900 dark:text-white">
            Budget Setup: {contract.contractNumber}
          </h1>
          <p className="text-sm text-gray-500 dark:text-gray-400 mt-1">
            {contract.contractTitle}
          </p>
        </div>
        <Button variant="outline" onClick={() => navigate(`/contracts/${contractId}`)}>
          Back to Contract
        </Button>
      </div>

      <div className="bg-white dark:bg-gray-800 shadow rounded-lg p-6 space-y-6">
        <div className="flex justify-between items-center">
          <div>
            <p className="text-sm text-gray-500 dark:text-gray-400">Contract Value</p>
            <p className="text-2xl font-bold text-gray-900 dark:text-white">
              ${contract.contractValue.toLocaleString()}
            </p>
          </div>
        </div>

        {/* Budget Template Selection */}
        {allTemplatesData?.data && (
          <div className="border-t border-gray-200 dark:border-gray-700 pt-6">
            <h2 className="text-lg font-semibold text-gray-900 dark:text-white mb-4">
              Budget Templates
            </h2>
            <p className="text-sm text-gray-500 dark:text-gray-400 mb-4">
              Select budget categories for this contract. Once added, templates cannot be removed.
            </p>
            
            <div className="grid grid-cols-2 md:grid-cols-3 gap-3">
              {allTemplatesData.data.map((template: BudgetTemplate) => {
                const isSelected = selectedTemplates.includes(template.id);
                const isDisabled = contractTemplatesData?.data?.some((t) => t.id === template.id);
                
                return (
                  <label
                    key={template.id}
                    className={`flex items-center p-3 border rounded-lg cursor-pointer ${
                      isSelected
                        ? 'bg-blue-50 border-blue-500 dark:bg-blue-900/20 dark:border-blue-400'
                        : 'bg-white border-gray-300 dark:bg-gray-700 dark:border-gray-600'
                    } ${isDisabled ? 'opacity-50 cursor-not-allowed' : 'hover:bg-gray-50 dark:hover:bg-gray-600'}`}
                  >
                    {/* Apply readonly mode if contract is approved - cannot remove templates */}
                    <PermissionGate 
                      permission="BUDGET_TEMPLATE_REMOVE" 
                      mode={isReadOnly && isDisabled ? "disabled" : undefined}
                    >
                      <input
                        type="checkbox"
                        checked={isSelected}
                        disabled={isDisabled || (isReadOnly && isDisabled)}
                        onChange={(e) => {
                          if (e.target.checked) {
                            setSelectedTemplates([...selectedTemplates, template.id]);
                          } else {
                            // Cannot remove if already saved (per PDF requirements)
                            if (!isDisabled) {
                              setSelectedTemplates(selectedTemplates.filter((id) => id !== template.id));
                            }
                          }
                        }}
                        className="mr-3 h-4 w-4 text-blue-600 rounded focus:ring-blue-500"
                      />
                    </PermissionGate>
                    <div>
                      <p className="text-sm font-medium text-gray-900 dark:text-white">
                        {template.name}
                      </p>
                      {template.description && (
                        <p className="text-xs text-gray-500 dark:text-gray-400">
                          {template.description}
                        </p>
                      )}
                    </div>
                  </label>
                );
              })}
            </div>

            <div className="flex justify-end mt-4">
              <Button
                variant="primary"
                onClick={() => {
                  const request: ContractBudgetTemplateRequest = {
                    contractId,
                    templateIds: selectedTemplates,
                  };
                  saveTemplatesMutation.mutate(request);
                }}
                disabled={saveTemplatesMutation.isPending || selectedTemplates.length === 0}
                isLoading={saveTemplatesMutation.isPending}
              >
                Save Templates
              </Button>
            </div>
          </div>
        )}

        <div className="border-t border-gray-200 dark:border-gray-700 pt-6">
          <h2 className="text-lg font-semibold text-gray-900 dark:text-white mb-4">
            Fiscal Year Budget Allocations
          </h2>

          <EditableTable
            columns={columns}
            data={budgetRows}
            onChange={setBudgetRows}
            createNewRow={createNewRow}
            validateRow={validateRow}
            plugins={[
              ExcelExportPlugin({ filename: `contract-${contractId}-budgets`, format: 'csv' }) as any,
              PrintPlugin({ title: `Contract ${contract?.contractNumber} - Budget Allocations` }) as any,
            ]}
            emptyMessage="No budget allocations. Click 'Add Row' to start."
          />

          {/* Fiscal Year Totals */}
          {Object.keys(totalsByFiscalYear).length > 0 && (
            <div className="mt-6 border-t border-gray-200 dark:border-gray-700 pt-4">
              <h3 className="text-md font-semibold text-gray-900 dark:text-white mb-2">
                Totals by Fiscal Year
              </h3>
              <div className="grid grid-cols-2 md:grid-cols-3 gap-4">
                {Object.entries(totalsByFiscalYear).map(([fiscalYear, total]) => (
                  <div key={fiscalYear} className="bg-gray-50 dark:bg-gray-700 p-3 rounded">
                    <p className="text-sm text-gray-500 dark:text-gray-400">FY {fiscalYear}</p>
                    <p className="text-lg font-semibold text-gray-900 dark:text-white">
                      ${total.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
                    </p>
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>

        <div className="flex justify-end space-x-4 border-t border-gray-200 dark:border-gray-700 pt-6">
          <Button
            variant="outline"
            onClick={() => navigate(`/contracts/${contractId}`)}
            disabled={saveMutation.isPending}
          >
            Cancel
          </Button>
          <Button
            variant="primary"
            onClick={handleSave}
            disabled={saveMutation.isPending || budgetRows.length === 0}
            isLoading={saveMutation.isPending}
          >
            Save Budget Allocations
          </Button>
        </div>
      </div>
    </div>
  );
};

