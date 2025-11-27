import { useNavigate } from 'react-router-dom';
import { Button } from '../../../../components/atoms/Button';

interface ContractConfigBudgetsTabProps {
  contractId: number;
}

/**
 * Contract Budgets Tab (Configuration Page)
 * Redirects to dedicated budget setup page
 */
export const ContractConfigBudgetsTab = ({ contractId }: ContractConfigBudgetsTabProps) => {
  const navigate = useNavigate();

  return (
    <div className="space-y-6">
      <div className="text-center py-12">
        <p className="text-lg text-gray-700 dark:text-gray-300 mb-4">
          Budget setup is available on the dedicated Budget page.
        </p>
        <Button
          variant="primary"
          onClick={() => navigate(`/contracts/${contractId}/budget`)}
        >
          Go to Budget Setup
        </Button>
      </div>
    </div>
  );
};

