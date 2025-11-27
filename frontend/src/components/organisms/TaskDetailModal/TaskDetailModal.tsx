import { useState } from 'react';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { Modal } from '../../molecules/Modal';
import { Button } from '../../atoms/Button';
import { Input } from '../../atoms/Input';
import { workflowService, WorkflowTask, TaskCompleteRequest } from '../../../services/workflowService';
import { useToast } from '../../../hooks/useToast';
import { WorkflowActionButtons } from '../../../frameworks/workflow/components/WorkflowActionButtons';
import { useAppSelector } from '../../../store/hooks';

interface TaskDetailModalProps {
  task: WorkflowTask;
  isOpen: boolean;
  onClose: () => void;
}

/**
 * Task detail modal organism component.
 * Displays task details and allows task completion.
 */
export const TaskDetailModal = ({ task, isOpen, onClose }: TaskDetailModalProps) => {
  const toast = useToast();
  const queryClient = useQueryClient();
  const user = useAppSelector((state) => state.auth.user);
  const [comments, setComments] = useState('');
  const [approved, setApproved] = useState<boolean | null>(null);

  const completeMutation = useMutation({
    mutationFn: (data: TaskCompleteRequest) => workflowService.completeTask(data.taskId, data.variables),
    onSuccess: (response: any) => {
      if (response.success) {
        toast.success('Task completed successfully!');
        queryClient.invalidateQueries({ queryKey: ['workflow-tasks'] });
        queryClient.invalidateQueries({ queryKey: ['contracts'] });
        onClose();
      } else {
        toast.error(response.error?.description || 'Failed to complete task');
      }
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.error?.description || 'An error occurred');
    },
  });

  const handleComplete = () => {
    if (approved === null) {
      toast.error('Please select Approve or Reject');
      return;
    }

    const request: TaskCompleteRequest = {
      taskId: task.taskKey,
      comments: comments || undefined,
      approved: approved,
      variables: task.variables,
    };

    completeMutation.mutate(request);
  };

  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      title={`Task: ${task.taskId}`}
      size="lg"
      footer={
        <Button variant="outline" onClick={onClose} disabled={completeMutation.isPending}>
          Close
        </Button>
      }
    >
      <div className="space-y-4">
        <div className="grid grid-cols-2 gap-4">
          <div>
            <p className="text-sm text-gray-500 dark:text-gray-400">Process</p>
            <p className="text-base font-medium text-gray-900 dark:text-white">
              {task.processDefinitionId}
            </p>
          </div>
          <div>
            <p className="text-sm text-gray-500 dark:text-gray-400">Contract</p>
            <p className="text-base font-medium text-gray-900 dark:text-white">
              {task.variables?.contractNumber || task.variables?.contractId || 'N/A'}
            </p>
          </div>
          <div>
            <p className="text-sm text-gray-500 dark:text-gray-400">Created</p>
            <p className="text-base font-medium text-gray-900 dark:text-white">
              {new Date(task.creationTime).toLocaleString()}
            </p>
          </div>
          <div>
            <p className="text-sm text-gray-500 dark:text-gray-400">Status</p>
            <span className="inline-block px-2 py-1 text-xs font-semibold rounded-full bg-blue-100 text-blue-800 dark:bg-blue-900/20 dark:text-blue-300">
              {task.state}
            </span>
          </div>
        </div>

        {task.variables && Object.keys(task.variables).length > 0 && (
          <div>
            <p className="text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
              Task Variables
            </p>
            <div className="bg-gray-50 dark:bg-gray-700 rounded p-3">
              <pre className="text-xs text-gray-600 dark:text-gray-300">
                {JSON.stringify(task.variables, null, 2)}
              </pre>
            </div>
          </div>
        )}

        <div>
          <p className="text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
            Decision
          </p>
          <div className="flex space-x-4">
            <label className="flex items-center">
              <input
                type="radio"
                name="decision"
                value="approve"
                checked={approved === true}
                onChange={() => setApproved(true)}
                className="mr-2"
              />
              <span className="text-sm text-gray-700 dark:text-gray-300">Approve</span>
            </label>
            <label className="flex items-center">
              <input
                type="radio"
                name="decision"
                value="reject"
                checked={approved === false}
                onChange={() => setApproved(false)}
                className="mr-2"
              />
              <span className="text-sm text-gray-700 dark:text-gray-300">Reject</span>
            </label>
          </div>
        </div>

        <Input
          label="Comments"
          value={comments}
          onChange={(e) => setComments(e.target.value)}
          placeholder="Enter comments (optional)"
          helperText="Optional comments for approval or rejection"
        />
      </div>
    </Modal>
  );
};




