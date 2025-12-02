import { useQuery } from '@tanstack/react-query';
import { useAppSelector } from '../../store/hooks';
import { Button } from '../../components/atoms/Button';
import { Card } from '../../components/atoms/Card';
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
    <div className="container-page section-spacing">
      <div className="flex justify-between items-center">
        <h1 className="text-responsive-xl font-bold text-gray-900 dark:text-white">My Tasks</h1>
      </div>

      {tasks.length === 0 ? (
        <Card className="text-center py-12">
          <p className="text-gray-500 dark:text-gray-400">No tasks assigned</p>
        </Card>
      ) : (
        <>
          {/* Desktop Table View */}
          <div className="desktop-only">
            <Card padding="none" className="overflow-hidden">
              <div className="table-container">
                <table className="table">
                  <thead className="table-header">
                    <tr>
                      <th className="table-header-cell">Task ID</th>
                      <th className="table-header-cell">Process</th>
                      <th className="table-header-cell">Contract</th>
                      <th className="table-header-cell">Created</th>
                      <th className="table-header-cell">Status</th>
                      <th className="table-header-cell text-right">Actions</th>
                    </tr>
                  </thead>
                  <tbody className="table-body">
                    {tasks.map((task: WorkflowTask) => (
                      <tr key={task.taskKey} className="table-row">
                        <td className="table-cell font-medium">{task.taskId}</td>
                        <td className="table-cell">{task.processDefinitionId}</td>
                        <td className="table-cell">
                          {task.variables?.contractNumber || task.variables?.contractId || 'N/A'}
                        </td>
                        <td className="table-cell">
                          {new Date(task.creationTime).toLocaleDateString()}
                        </td>
                        <td className="table-cell">
                          <span className="badge bg-blue-100 text-blue-800 dark:bg-blue-900/20 dark:text-blue-300">
                            {task.state}
                          </span>
                        </td>
                        <td className="table-cell text-right">
                          <div className="flex items-center justify-end gap-2">
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
            </Card>
          </div>

          {/* Mobile Card View */}
          <div className="mobile-only space-y-4">
            {tasks.map((task: WorkflowTask) => (
              <Card key={task.taskKey} hover className="section-spacing">
                <div className="flex items-start justify-between mb-3">
                  <div className="flex-1 min-w-0">
                    <h3 className="text-lg font-semibold text-gray-900 dark:text-white truncate">
                      {task.taskId}
                    </h3>
                    <p className="text-sm text-gray-600 dark:text-gray-400 mt-1">
                      {task.processDefinitionId}
                    </p>
                  </div>
                  <span className="badge ml-2 flex-shrink-0 bg-blue-100 text-blue-800 dark:bg-blue-900/20 dark:text-blue-300">
                    {task.state}
                  </span>
                </div>
                
                <div className="grid grid-cols-2 gap-4 text-sm mb-4">
                  <div>
                    <p className="text-gray-500 dark:text-gray-400 text-xs">Contract</p>
                    <p className="font-medium text-gray-900 dark:text-white">
                      {task.variables?.contractNumber || task.variables?.contractId || 'N/A'}
                    </p>
                  </div>
                  <div>
                    <p className="text-gray-500 dark:text-gray-400 text-xs">Created</p>
                    <p className="font-medium text-gray-900 dark:text-white">
                      {new Date(task.creationTime).toLocaleDateString()}
                    </p>
                  </div>
                </div>

                <div className="flex gap-2 pt-4 border-t border-gray-200 dark:border-gray-700">
                  <Button
                    variant="outline"
                    size="sm"
                    onClick={() => handleViewWorkflow(task)}
                    className="flex-1"
                  >
                    View Workflow
                  </Button>
                  <Button
                    variant="primary"
                    size="sm"
                    onClick={() => handleViewTask(task)}
                    className="flex-1"
                  >
                    View & Complete
                  </Button>
                </div>
              </Card>
            ))}
          </div>
        </>
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
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <div className="bg-white dark:bg-gray-800 rounded-lg p-4 sm:p-6 max-w-5xl w-full max-h-[90vh] overflow-auto">
            <div className="flex flex-col sm:flex-row sm:justify-between sm:items-center gap-4 mb-4">
              <h3 className="text-lg sm:text-xl font-semibold text-gray-900 dark:text-white">
                Workflow Status Visualization
              </h3>
              <Button
                variant="outline"
                size="sm"
                onClick={() => setSelectedWorkflowInstance(null)}
                className="w-full sm:w-auto"
              >
                Close
              </Button>
            </div>
            <div className="overflow-x-auto">
              <BpmnDiagramViewer
                processDefinitionKey={selectedWorkflowInstance.processDefinitionKey}
                processInstanceKey={selectedWorkflowInstance.processInstanceKey}
                height="400px"
                className="sm:h-[600px]"
              />
            </div>
          </div>
        </div>
      )}
    </div>
  );
};




