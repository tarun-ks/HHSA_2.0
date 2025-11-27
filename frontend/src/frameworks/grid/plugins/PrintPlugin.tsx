import { ReactNode } from 'react';
import { Button } from '../../../components/atoms/Button';
import { GridPlugin, GridPluginProps } from './GridPlugin.types';

export interface PrintConfig {
  title?: string;
  includeHeaders?: boolean;
}

/**
 * Print Plugin
 * Adds print functionality to EditableTable
 */
export function PrintPlugin<T extends Record<string, any>>(
  config: PrintConfig = {}
): GridPlugin<T> {
  const { title = 'Table', includeHeaders = true } = config;

  return {
    name: 'print',
    priority: 20,
    renderToolbar: (props: GridPluginProps<T>) => {
      const { data, columns } = props;

      if (!data || data.length === 0) {
        return null;
      }

      const handlePrint = () => {
        // Create print window
        const printWindow = window.open('', '_blank');
        if (!printWindow) {
          alert('Please allow popups to print');
          return;
        }

        // Build HTML table
        let html = `
          <!DOCTYPE html>
          <html>
            <head>
              <title>${title}</title>
              <style>
                body { font-family: Arial, sans-serif; margin: 20px; }
                table { border-collapse: collapse; width: 100%; }
                th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
                th { background-color: #f2f2f2; font-weight: bold; }
                tr:nth-child(even) { background-color: #f9f9f9; }
                @media print {
                  body { margin: 0; }
                  @page { margin: 1cm; }
                }
              </style>
            </head>
            <body>
              <h2>${title}</h2>
              <table>
        `;

        // Add headers
        if (includeHeaders) {
          html += '<thead><tr>';
          columns.forEach((col: any) => {
            html += `<th>${col.label || col.key}</th>`;
          });
          html += '</tr></thead>';
        }

        // Add rows
        html += '<tbody>';
        data.forEach((row) => {
          html += '<tr>';
          columns.forEach((col: any) => {
            const value = row[col.key];
            html += `<td>${value != null ? String(value) : ''}</td>`;
          });
          html += '</tr>';
        });
        html += '</tbody></table></body></html>';

        printWindow.document.write(html);
        printWindow.document.close();
        printWindow.focus();
        setTimeout(() => {
          printWindow.print();
          printWindow.close();
        }, 250);
      };

      return (
        <Button
          variant="outline"
          size="sm"
          onClick={handlePrint}
          className="ml-2"
        >
          🖨️ Print
        </Button>
      );
    },
  };
}

