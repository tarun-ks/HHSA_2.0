import { useState, useCallback, useMemo, ReactNode } from 'react';
import { Button } from '../../atoms/Button';
import { cn } from '../../../utils/cn';
import { GridPlugin, GridPluginProps } from '../../../frameworks/grid/plugins/GridPlugin.types';
import { useGridPlugins } from '../../../frameworks/grid/hooks/useGridPlugins';

/**
 * Column definition for editable table
 */
export interface EditableTableColumn<T> {
  /** Unique key for the column */
  key: string;
  /** Column header label */
  label: string;
  /** Function to render cell content (read mode) */
  render?: (row: T, index: number) => ReactNode;
  /** Function to render editable cell (edit mode) */
  renderEdit?: (row: T, index: number, onChange: (value: any) => void) => ReactNode;
  /** Column width (optional) */
  width?: string;
  /** Whether column is required */
  required?: boolean;
  /** Validation function for cell value */
  validate?: (value: any, row: T, allRows: T[]) => string | undefined;
}

/**
 * Props for EditableTable component
 */
export interface EditableTableProps<T> {
  /** Column definitions */
  columns: EditableTableColumn<T>[];
  /** Initial data rows */
  data: T[];
  /** Callback when data changes */
  onChange: (data: T[]) => void;
  /** Callback to create new empty row */
  createNewRow: () => T;
  /** Optional: Custom validation for entire row */
  validateRow?: (row: T, allRows: T[]) => Record<string, string> | null;
  /** Optional: Whether table is read-only */
  readOnly?: boolean;
  /** Optional: Show add/remove buttons */
  showActions?: boolean;
  /** Optional: Custom empty state message */
  emptyMessage?: string;
  /** Optional: Additional CSS classes */
  className?: string;
  /** Optional: Grid plugins for extensibility */
  plugins?: GridPlugin<T>[];
}

/**
 * EditableTable organism component.
 * Provides inline editing capability for table rows with add/delete functionality.
 * Follows atomic design pattern (organism level).
 */
export function EditableTable<T extends Record<string, any>>({
  columns,
  data,
  onChange,
  createNewRow,
  validateRow,
  readOnly = false,
  showActions = true,
  emptyMessage = 'No data. Click "Add Row" to start.',
  className,
  plugins = [],
}: EditableTableProps<T>) {
  const [editingRow, setEditingRow] = useState<number | null>(null);
  const [rowErrors, setRowErrors] = useState<Record<number, Record<string, string>>>({});

  // Initialize grid plugins
  const pluginProps: GridPluginProps<T> = useMemo(
    () => ({
      data,
      columns,
      onChange,
    }),
    [data, columns, onChange]
  );

  const {
    renderToolbar,
    notifyDataChange,
  } = useGridPlugins(plugins);

  // Notify plugins of data changes
  const handleDataChange = useCallback(
    (newData: T[]) => {
      onChange(newData);
      notifyDataChange(newData, pluginProps);
    },
    [onChange, notifyDataChange, pluginProps]
  );

  /**
   * Handle adding a new row
   */
  const handleAddRow = useCallback(() => {
    const newRow = createNewRow();
    const newData = [...data, newRow];
    handleDataChange(newData);
    // Auto-edit the new row
    setEditingRow(data.length);
  }, [data, createNewRow, handleDataChange]);

  /**
   * Handle deleting a row
   */
  const handleDeleteRow = useCallback(
    (index: number) => {
      const newData = data.filter((_, i) => i !== index);
      handleDataChange(newData);
      // Clear errors for deleted row
      const newErrors = { ...rowErrors };
      delete newErrors[index];
      setRowErrors(newErrors);
      // Adjust editing row index if needed
      if (editingRow !== null && editingRow >= newData.length) {
        setEditingRow(null);
      }
    },
    [data, handleDataChange, rowErrors, editingRow]
  );

  /**
   * Handle cell value change
   */
  const handleCellChange = useCallback(
    (rowIndex: number, columnKey: string, value: any) => {
      const newData = [...data];
      newData[rowIndex] = { ...newData[rowIndex], [columnKey]: value };
      handleDataChange(newData);

      // Validate cell
      const column = columns.find((col) => col.key === columnKey);
      if (column?.validate) {
        const error = column.validate(value, newData[rowIndex], newData);
        const newErrors = { ...rowErrors };
        if (!newErrors[rowIndex]) {
          newErrors[rowIndex] = {};
        }
        if (error) {
          newErrors[rowIndex][columnKey] = error;
        } else {
          delete newErrors[rowIndex][columnKey];
          if (Object.keys(newErrors[rowIndex]).length === 0) {
            delete newErrors[rowIndex];
          }
        }
        setRowErrors(newErrors);
      }

      // Validate entire row if validator provided
      if (validateRow) {
        const rowValidationErrors = validateRow(newData[rowIndex], newData);
        const newErrors: Record<number, Record<string, string>> = { ...rowErrors };
        if (rowValidationErrors) {
          if (!newErrors[rowIndex]) {
            newErrors[rowIndex] = {};
          }
          Object.assign(newErrors[rowIndex], rowValidationErrors);
        } else {
          // Clear row-level errors
          if (newErrors[rowIndex]) {
            Object.keys(newErrors[rowIndex]).forEach((key) => {
              if (!columns.find((col) => col.key === key)?.validate) {
                delete newErrors[rowIndex][key];
              }
            });
            if (Object.keys(newErrors[rowIndex]).length === 0) {
              delete newErrors[rowIndex];
            }
          }
        }
        setRowErrors(newErrors);
      }
    },
    [data, handleDataChange, columns, rowErrors, validateRow]
  );

  /**
   * Toggle edit mode for a row
   */
  const handleToggleEdit = useCallback(
    (index: number) => {
      if (editingRow === index) {
        // Stop editing - validate row
        if (validateRow) {
          const errors = validateRow(data[index], data);
          if (errors) {
            const newErrors = { ...rowErrors };
            newErrors[index] = { ...newErrors[index], ...errors };
            setRowErrors(newErrors);
            return; // Don't stop editing if there are errors
          }
        }
        setEditingRow(null);
      } else {
        setEditingRow(index);
      }
    },
    [editingRow, data, validateRow, rowErrors]
  );

  /**
   * Get error message for a cell
   */
  const getCellError = (rowIndex: number, columnKey: string): string | undefined => {
    return rowErrors[rowIndex]?.[columnKey];
  };

  /**
   * Check if row has errors
   */
  const hasRowErrors = (rowIndex: number): boolean => {
    return rowErrors[rowIndex] && Object.keys(rowErrors[rowIndex]).length > 0;
  };

  // Render plugin toolbars
  const toolbarPlugins = renderToolbar(pluginProps);

  return (
    <div className={cn('space-y-4', className)}>
      {showActions && !readOnly && (
        <div className="flex justify-end items-center gap-2">
          {toolbarPlugins}
          <Button variant="primary" size="sm" onClick={handleAddRow}>
            Add Row
          </Button>
        </div>
      )}
      {!showActions && toolbarPlugins.length > 0 && (
        <div className="flex justify-end items-center gap-2">
          {toolbarPlugins}
        </div>
      )}

      {data.length === 0 ? (
        <div className="text-center py-8 text-gray-500 dark:text-gray-400">{emptyMessage}</div>
      ) : (
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200 dark:divide-gray-700">
            <thead className="bg-gray-50 dark:bg-gray-700">
              <tr>
                {columns.map((column) => (
                  <th
                    key={column.key}
                    className="px-4 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider"
                    style={column.width ? { width: column.width } : undefined}
                  >
                    {column.label}
                    {column.required && (
                      <span className="text-red-500 ml-1" aria-label="required">
                        *
                      </span>
                    )}
                  </th>
                ))}
                {showActions && !readOnly && (
                  <th className="px-4 py-3 text-right text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                    Actions
                  </th>
                )}
              </tr>
            </thead>
            <tbody className="bg-white dark:bg-gray-800 divide-y divide-gray-200 dark:divide-gray-700">
              {data.map((row, rowIndex) => {
                const isEditing = editingRow === rowIndex;
                const hasErrors = hasRowErrors(rowIndex);

                return (
                  <tr
                    key={rowIndex}
                    className={cn(
                      'hover:bg-gray-50 dark:hover:bg-gray-700',
                      hasErrors && 'bg-red-50 dark:bg-red-900/10'
                    )}
                  >
                    {columns.map((column) => {
                      const error = getCellError(rowIndex, column.key);
                      const cellValue = row[column.key];

                      return (
                        <td
                          key={column.key}
                          className={cn(
                            'px-4 py-3 text-sm',
                            error && 'border-l-4 border-red-500'
                          )}
                        >
                          {isEditing && !readOnly && column.renderEdit ? (
                            <div>
                              {column.renderEdit(row, rowIndex, (value) =>
                                handleCellChange(rowIndex, column.key, value)
                              )}
                              {error && (
                                <p className="mt-1 text-xs text-red-600 dark:text-red-400">
                                  {error}
                                </p>
                              )}
                            </div>
                          ) : (
                            <div>
                              {column.render ? (
                                column.render(row, rowIndex)
                              ) : (
                                <span className="text-gray-900 dark:text-white">
                                  {cellValue != null ? String(cellValue) : ''}
                                </span>
                              )}
                            </div>
                          )}
                        </td>
                      );
                    })}
                    {showActions && !readOnly && (
                      <td className="px-4 py-3 text-right text-sm font-medium space-x-2">
                        {isEditing ? (
                          <Button
                            variant="ghost"
                            size="sm"
                            onClick={() => handleToggleEdit(rowIndex)}
                            disabled={hasErrors}
                          >
                            Done
                          </Button>
                        ) : (
                          <Button
                            variant="ghost"
                            size="sm"
                            onClick={() => handleToggleEdit(rowIndex)}
                          >
                            Edit
                          </Button>
                        )}
                        <Button
                          variant="ghost"
                          size="sm"
                          onClick={() => handleDeleteRow(rowIndex)}
                          className="text-red-600 hover:text-red-800 dark:text-red-400 dark:hover:text-red-300"
                        >
                          Delete
                        </Button>
                      </td>
                    )}
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}

