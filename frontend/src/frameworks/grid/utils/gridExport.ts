/**
 * Grid export utilities
 */

/**
 * Convert data to CSV format
 */
export function exportToCSV<T extends Record<string, any>>(
  data: T[],
  columns: Array<{ key: string; label: string }>,
  filename: string = 'export.csv'
): void {
  if (data.length === 0) {
    alert('No data to export');
    return;
  }

  // Create CSV header
  const headers = columns.map((col) => col.label).join(',');
  
  // Create CSV rows
  const rows = data.map((row) =>
    columns.map((col) => {
      const value = row[col.key];
      // Escape commas and quotes in CSV
      if (value === null || value === undefined) return '';
      const stringValue = String(value);
      if (stringValue.includes(',') || stringValue.includes('"') || stringValue.includes('\n')) {
        return `"${stringValue.replace(/"/g, '""')}"`;
      }
      return stringValue;
    }).join(',')
  );

  // Combine header and rows
  const csvContent = [headers, ...rows].join('\n');

  // Create blob and download
  const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
  const link = document.createElement('a');
  const url = URL.createObjectURL(blob);
  link.setAttribute('href', url);
  link.setAttribute('download', filename);
  link.style.visibility = 'hidden';
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
  URL.revokeObjectURL(url);
}

/**
 * Convert data to JSON format
 */
export function exportToJSON<T>(
  data: T[],
  filename: string = 'export.json'
): void {
  if (data.length === 0) {
    alert('No data to export');
    return;
  }

  const jsonContent = JSON.stringify(data, null, 2);
  const blob = new Blob([jsonContent], { type: 'application/json;charset=utf-8;' });
  const link = document.createElement('a');
  const url = URL.createObjectURL(blob);
  link.setAttribute('href', url);
  link.setAttribute('download', filename);
  link.style.visibility = 'hidden';
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
  URL.revokeObjectURL(url);
}

/**
 * Export to Excel (XLSX) format
 * Note: This is a simplified version. For full Excel support, consider using a library like 'xlsx'
 */
export function exportToExcel<T extends Record<string, any>>(
  data: T[],
  columns: Array<{ key: string; label: string }>,
  filename: string = 'export.xlsx',
  sheetName: string = 'Sheet1'
): void {
  // For now, export as CSV with .xlsx extension
  // In production, use a library like 'xlsx' for proper Excel format
  console.warn('Excel export using CSV format. Install "xlsx" library for full Excel support.');
  exportToCSV(data, columns, filename.replace('.xlsx', '.csv'));
}

