import { DocumentUploadSection } from '../DocumentUploadSection';

interface ContractConfigDocumentsTabProps {
  contractId: number;
}

/**
 * Contract Documents Tab (Configuration Page)
 * Displays document upload and management
 */
export const ContractConfigDocumentsTab = ({ contractId }: ContractConfigDocumentsTabProps) => {
  return (
    <div className="space-y-6">
      <div>
        <h3 className="text-lg font-semibold text-gray-900 dark:text-white mb-4">
          Contract Documents
        </h3>
        <DocumentUploadSection contractId={contractId} entityType="Contract" />
      </div>
    </div>
  );
};

