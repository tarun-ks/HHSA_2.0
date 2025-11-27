import { CommentsSection } from '../CommentsSection';

interface ContractConfigCommentsTabProps {
  contractId: number;
}

/**
 * Contract Comments Tab (Configuration Page)
 * Displays comments section
 */
export const ContractConfigCommentsTab = ({ contractId }: ContractConfigCommentsTabProps) => {
  return (
    <div className="space-y-6">
      <div>
        <h3 className="text-lg font-semibold text-gray-900 dark:text-white mb-4">
          Comments
        </h3>
        <CommentsSection contractId={contractId} />
      </div>
    </div>
  );
};

