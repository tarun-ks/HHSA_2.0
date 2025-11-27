import { useState } from 'react';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { Button } from '../../../components/atoms/Button';
import { workflowService, WorkflowTask } from '../../../services/workflowService';
import { useToast } from '../../../hooks/useToast';
import { useAppSelector } from '../../../store/hooks';
import { UserSelectionModal } from './UserSelectionModal';

interface WorkflowActionButtonsProps {
  task: WorkflowTask;
  currentUserId: string;
  onActionComplete?: () => void;
  availableActions?: ('assign' | 'claim' | 'unclaim' | 'reassign' | 'return' | 'complete')[];
  showComplete?: boolean;
  onComplete?: () => void;
}

/**
 * Reusable component for workflow action buttons.
 * Works for any workflow task - assign, claim, reassign, return, etc.
 */
export const WorkflowActionButtons = ({
  task,
  currentUserId,
  onActionComplete,
  availableActions = ['complete', 'reassign', 'return'],
  showComplete = true,
  onComplete,
}: WorkflowActionButtonsProps) => {
  const toast = useToast();
  const queryClient = useQueryClient();
  const [showUserModal, setShowUserModal] = useState(false);
  const [actionType, setActionType] = useState<'assign' | 'reassign' | null>(null);
  const [showReturnModal, setShowReturnModal] = useState(false);
  const [returnReason, setReturnReason] = useState('');

  const user = useAppSelector((state) => state.auth.user);

  const invalidateQueries = () => {
    queryClient.invalidateQueries({ queryKey: ['workflow-tasks'] });
    queryClient.invalidateQueries({ queryKey: ['tasks', task.processInstanceKey] });
    queryClient.invalidateQueries({ queryKey: ['user-tasks', currentUserId] });
    onActionComplete?.();
  };

  const assignMutation = useMutation({
    mutationFn: (userId: string) => workflowService.assignTask(task.taskKey, userId),
    onSuccess: () => {
      toast.success('Task assigned successfully');
      invalidateQueries();
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.error?.description || 'Failed to assign task');
    },
  });

  const claimMutation = useMutation({
    mutationFn: () => workflowService.claimTask(task.taskKey, currentUserId),
    onSuccess: () => {
      toast.success('Task claimed successfully');
      invalidateQueries();
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.error?.description || 'Failed to claim task');
    },
  });

  const unclaimMutation = useMutation({
    mutationFn: () => workflowService.unclaimTask(task.taskKey),
    onSuccess: () => {
      toast.success('Task unclaimed successfully');
      invalidateQueries();
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.error?.description || 'Failed to unclaim task');
    },
  });

  const reassignMutation = useMutation({
    mutationFn: (userId: string) => workflowService.reassignTask(task.taskKey, userId),
    onSuccess: () => {
      toast.success('Task reassigned successfully');
      invalidateQueries();
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.error?.description || 'Failed to reassign task');
    },
  });

  const returnMutation = useMutation({
    mutationFn: (reason?: string) => workflowService.returnTask(task.taskKey, reason),
    onSuccess: () => {
      toast.success('Task returned successfully');
      invalidateQueries();
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.error?.description || 'Failed to return task');
    },
  });

  const handleUserSelect = (userId: string) => {
    if (actionType === 'assign') {
      assignMutation.mutate(userId);
    } else if (actionType === 'reassign') {
      reassignMutation.mutate(userId);
    }
    setShowUserModal(false);
    setActionType(null);
  };

  const handleReturn = () => {
    if (returnReason.trim()) {
      returnMutation.mutate(returnReason);
      setShowReturnModal(false);
      setReturnReason('');
    } else {
      toast.error('Please provide a reason for returning the task');
    }
  };

  const isTaskAssigned = task.assignee != null;
  const isAssignedToCurrentUser = task.assignee === currentUserId;
  const canPerformActions = task.state === 'CREATED';

  return (
    <>
      <div className="flex flex-wrap gap-2">
        {/* Claim button - for unassigned tasks */}
        {availableActions.includes('claim') && !isTaskAssigned && canPerformActions && (
          <Button
            variant="outline"
            onClick={() => claimMutation.mutate()}
            disabled={claimMutation.isPending}
            isLoading={claimMutation.isPending}
          >
            Claim
          </Button>
        )}

        {/* Assign button - for unassigned tasks */}
        {availableActions.includes('assign') && !isTaskAssigned && canPerformActions && (
          <Button
            variant="outline"
            onClick={() => {
              setActionType('assign');
              setShowUserModal(true);
            }}
            disabled={assignMutation.isPending}
          >
            Assign
          </Button>
        )}

        {/* Unclaim button - for tasks assigned to current user */}
        {availableActions.includes('unclaim') && isAssignedToCurrentUser && canPerformActions && (
          <Button
            variant="outline"
            onClick={() => unclaimMutation.mutate()}
            disabled={unclaimMutation.isPending}
            isLoading={unclaimMutation.isPending}
          >
            Unclaim
          </Button>
        )}

        {/* Reassign button */}
        {availableActions.includes('reassign') && isTaskAssigned && canPerformActions && (
          <Button
            variant="outline"
            onClick={() => {
              setActionType('reassign');
              setShowUserModal(true);
            }}
            disabled={reassignMutation.isPending}
          >
            Reassign
          </Button>
        )}

        {/* Return button */}
        {availableActions.includes('return') && isAssignedToCurrentUser && canPerformActions && (
          <Button
            variant="outline"
            onClick={() => setShowReturnModal(true)}
            disabled={returnMutation.isPending}
          >
            Return
          </Button>
        )}

        {/* Complete button */}
        {showComplete && availableActions.includes('complete') && isAssignedToCurrentUser && canPerformActions && (
          <Button
            variant="primary"
            onClick={onComplete}
          >
            Complete Task
          </Button>
        )}
      </div>

      {/* User Selection Modal */}
      <UserSelectionModal
        isOpen={showUserModal}
        onClose={() => {
          setShowUserModal(false);
          setActionType(null);
        }}
        onSelect={handleUserSelect}
        title={actionType === 'assign' ? 'Assign Task' : 'Reassign Task'}
        excludeUserIds={task.assignee ? [task.assignee] : []}
      />

      {/* Return Reason Modal */}
      {showReturnModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white dark:bg-gray-800 rounded-lg p-6 max-w-md w-full mx-4">
            <h3 className="text-lg font-semibold text-gray-900 dark:text-white mb-4">
              Return Task
            </h3>
            <div className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                  Reason (required)
                </label>
                <textarea
                  value={returnReason}
                  onChange={(e) => setReturnReason(e.target.value)}
                  className="w-full px-3 py-2 border rounded-lg dark:bg-gray-700 dark:text-white dark:border-gray-600"
                  rows={4}
                  placeholder="Enter reason for returning the task..."
                />
              </div>
              <div className="flex justify-end space-x-2">
                <Button
                  variant="outline"
                  onClick={() => {
                    setShowReturnModal(false);
                    setReturnReason('');
                  }}
                >
                  Cancel
                </Button>
                <Button
                  variant="primary"
                  onClick={handleReturn}
                  disabled={returnMutation.isPending || !returnReason.trim()}
                  isLoading={returnMutation.isPending}
                >
                  Return Task
                </Button>
              </div>
            </div>
          </div>
        </div>
      )}
    </>
  );
};

