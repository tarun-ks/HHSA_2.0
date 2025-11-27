import { ReactNode } from 'react';
import { Button } from '../../../components/atoms/Button';
import { GridPlugin, GridPluginProps } from './GridPlugin.types';
import { exportToExcel, exportToCSV } from '../utils/gridExport';

export interface ExcelExportConfig {
  filename?: string;
  sheetName?: string;
  format?: 'xlsx' | 'csv';
}

/**
 * Excel/CSV Export Plugin
 * Adds export functionality to EditableTable
 */
export function ExcelExportPlugin<T extends Record<string, any>>(
  config: ExcelExportConfig = {}
): GridPlugin<T> {
  const {
    filename = 'export',
    sheetName = 'Sheet1',
    format = 'csv', // Default to CSV (xlsx requires xlsx library)
  } = config;

  return {
    name: 'excel-export',
    priority: 10,
    renderToolbar: (props: GridPluginProps<T>) => {
      const { data, columns } = props;

      if (!data || data.length === 0) {
        return null;
      }

      const handleExport = () => {
        const columnDefs = columns.map((col: any) => ({
          key: col.key,
          label: col.label || col.key,
        }));

        if (format === 'xlsx') {
          exportToExcel(data, columnDefs, `${filename}.xlsx`, sheetName);
        } else {
          exportToCSV(data, columnDefs, `${filename}.csv`);
        }
      };

      return (
        <Button
          variant="outline"
          size="sm"
          onClick={handleExport}
          className="ml-2"
        >
          {format === 'xlsx' ? '📊 Export Excel' : '📄 Export CSV'}
        </Button>
      );
    },
  };
}

