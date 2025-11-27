import { DocumentUploadSection } from '../DocumentUploadSection';

interface DocumentDetailsTabProps {
  contractId: number;
}

/**
 * Document Details Tab
 * Displays contract-related documents with upload functionality
 */
export const DocumentDetailsTab = ({ contractId }: DocumentDetailsTabProps) => {
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

