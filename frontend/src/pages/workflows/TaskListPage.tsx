import { useQuery } from '@tanstack/react-query';
import { useAppSelector } from '../../store/hooks';
import { Button } from '../../components/atoms/Button';
import { workflowService, WorkflowTask } from '../../services/workflowService';
import { useToast } from '../../hooks/useToast';
import { Loader } from '../../components/atoms/Loader';
import { useState } from 'react';
import { TaskDetailModal } from '../../components/organisms/TaskDetailModal/TaskDetailModal';
import { BpmnDiagramViewer } from '../../frameworks/workflow/components/BpmnDiagramViewer';

/**
 * Workflow task list page.
 * Displays all tasks assigned to the current user.
 */
export const TaskListPage = () => {
  const user = useAppSelector((state) => state.auth.user);
  const toast = useToast();
  const [selectedTask, setSelectedTask] = useState<WorkflowTask | null>(null);
  const [showTaskModal, setShowTaskModal] = useState(false);
  const [selectedWorkflowInstance, setSelectedWorkflowInstance] = useState<{
    processInstanceKey: number;
    processDefinitionKey: string;
  } | null>(null);

  const { data, isLoading, error, refetch } = useQuery({
    queryKey: ['workflow-tasks', user?.id],
    queryFn: () => workflowService.getUserTasks(user?.id || ''),
    enabled: !!user?.id,
  });

  const handleViewTask = (task: WorkflowTask) => {
    setSelectedTask(task);
    setShowTaskModal(true);
  };

  const handleViewWorkflow = (task: WorkflowTask) => {
    // Use process definition ID directly - framework-based
    if (task.processInstanceKey && task.processDefinitionId) {
      setSelectedWorkflowInstance({
        processInstanceKey: task.processInstanceKey,
        processDefinitionKey: task.processDefinitionId,
      });
    }
  };

  if (isLoading) {
    return (
      <div className="flex justify-center items-center h-64">
        <Loader size="lg" />
      </div>
    );
  }

  if (error) {
    toast.error('Failed to load tasks');
    return null;
  }

  const tasks = data?.data || [];

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h1 className="text-2xl font-bold text-gray-900 dark:text-white">My Tasks</h1>
      </div>

      {tasks.length === 0 ? (
        <div className="text-center py-12">
          <p className="text-gray-500 dark:text-gray-400">No tasks assigned</p>
        </div>
      ) : (
        <div className="bg-white dark:bg-gray-800 shadow rounded-lg overflow-hidden">
          <table className="min-w-full divide-y divide-gray-200 dark:divide-gray-700">
            <thead className="bg-gray-50 dark:bg-gray-700">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase">
                  Task ID
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase">
                  Process
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase">
                  Contract
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase">
                  Created
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase">
                  Status
                </th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 dark:text-gray-300 uppercase">
                  Actions
                </th>
              </tr>
            </thead>
            <tbody className="bg-white dark:bg-gray-800 divide-y divide-gray-200 dark:divide-gray-700">
              {tasks.map((task: WorkflowTask) => (
                <tr key={task.taskKey} className="hover:bg-gray-50 dark:hover:bg-gray-700">
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900 dark:text-white">
                    {task.taskId}
                  </td>
                  <td className="px-6 py-4 text-sm text-gray-500 dark:text-gray-300">
                    {task.processDefinitionId}
                  </td>
                  <td className="px-6 py-4 text-sm text-gray-500 dark:text-gray-300">
                    {task.variables?.contractNumber || task.variables?.contractId || 'N/A'}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500 dark:text-gray-300">
                    {new Date(task.creationTime).toLocaleDateString()}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <span className="px-2 py-1 text-xs font-semibold rounded-full bg-blue-100 text-blue-800 dark:bg-blue-900/20 dark:text-blue-300">
                      {task.state}
                    </span>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                    <div className="flex justify-end space-x-2">
                      <Button
                        variant="outline"
                        size="sm"
                        onClick={() => handleViewWorkflow(task)}
                      >
                        View Workflow
                      </Button>
                      <Button
                        variant="primary"
                        size="sm"
                        onClick={() => handleViewTask(task)}
                      >
                        View & Complete
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {selectedTask && (
        <TaskDetailModal
          task={selectedTask}
          isOpen={showTaskModal}
          onClose={() => {
            setShowTaskModal(false);
            setSelectedTask(null);
            refetch();
          }}
        />
      )}

      {/* Workflow Visualization Modal */}
      {selectedWorkflowInstance && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white dark:bg-gray-800 rounded-lg p-6 max-w-5xl w-full mx-4 max-h-[90vh] overflow-auto">
            <div className="flex justify-between items-center mb-4">
              <h3 className="text-lg font-semibold text-gray-900 dark:text-white">
                Workflow Status Visualization
              </h3>
              <Button
                variant="outline"
                size="sm"
                onClick={() => setSelectedWorkflowInstance(null)}
              >
                Close
              </Button>
            </div>
            <BpmnDiagramViewer
              processDefinitionKey={selectedWorkflowInstance.processDefinitionKey}
              processInstanceKey={selectedWorkflowInstance.processInstanceKey}
              height="600px"
            />
          </div>
        </div>
      )}
    </div>
  );
};




