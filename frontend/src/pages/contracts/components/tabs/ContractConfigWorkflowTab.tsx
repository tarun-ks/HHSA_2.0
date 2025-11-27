import { BpmnDiagramViewer } from '../../../../frameworks/workflow/components/BpmnDiagramViewer';

interface ContractConfigWorkflowTabProps {
  processInstanceKey?: number;
}

/**
 * Contract Workflow Tab (Configuration Page)
 * Displays workflow BPMN diagram
 */
export const ContractConfigWorkflowTab = ({ processInstanceKey }: ContractConfigWorkflowTabProps) => {
  if (!processInstanceKey) {
    return (
      <div className="text-center py-12 text-gray-500 dark:text-gray-400">
        <p>No workflow instance found for this contract.</p>
        <p className="text-sm mt-2">Workflow will be created when contract is configured.</p>
      </div>
    );
  }

  return (
    <div className="space-y-4">
      <BpmnDiagramViewer
        processDefinitionKey="ContractConfiguration"
        processInstanceKey={processInstanceKey}
        height="600px"
      />
    </div>
  );
};

