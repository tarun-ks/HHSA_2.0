import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { Button } from '../../../components/atoms/Button';
import { contractService, ContractCommentRequest } from '../../../services/contractService';
import { useToast } from '../../../hooks/useToast';
import { Loader } from '../../../components/atoms/Loader';
import { useAppSelector } from '../../../store/hooks';

interface CommentsSectionProps {
  contractId: number;
  taskId?: string;
}

/**
 * Comments Section Component
 * Allows users to add and view comments for a contract
 */
export const CommentsSection = ({ contractId, taskId }: CommentsSectionProps) => {
  const toast = useToast();
  const queryClient = useQueryClient();
  const user = useAppSelector((state) => state.auth.user);
  const [newComment, setNewComment] = useState<string>('');

  const { data: commentsData, isLoading } = useQuery({
    queryKey: ['contract-comments', contractId, taskId],
    queryFn: () => contractService.getContractComments(contractId),
    enabled: !!contractId,
  });

  const createMutation = useMutation({
    mutationFn: (data: ContractCommentRequest) =>
      contractService.createContractComment(contractId, data),
    onSuccess: (response) => {
      if (response.success) {
        toast.success('Comment added successfully!');
        queryClient.invalidateQueries({ queryKey: ['contract-comments', contractId, taskId] });
        setNewComment('');
      } else {
        toast.error(response.error?.description || 'Failed to add comment');
      }
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.error?.description || 'An error occurred');
    },
  });

  const deleteMutation = useMutation({
    mutationFn: (commentId: number) => contractService.deleteContractComment(contractId, commentId),
    onSuccess: (response) => {
      if (response.success) {
        toast.success('Comment deleted successfully!');
        queryClient.invalidateQueries({ queryKey: ['contract-comments', contractId, taskId] });
      } else {
        toast.error(response.error?.description || 'Failed to delete comment');
      }
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.error?.description || 'An error occurred');
    },
  });

  const handleSubmit = () => {
    if (!newComment.trim()) {
      toast.error('Please enter a comment');
      return;
    }

    const request: ContractCommentRequest = {
      contractId,
      taskId,
      commentText: newComment.trim(),
    };

    createMutation.mutate(request);
  };

  const handleDelete = (commentId: number) => {
    if (window.confirm('Are you sure you want to delete this comment?')) {
      deleteMutation.mutate(commentId);
    }
  };

  const comments = commentsData?.data || [];

  return (
    <div className="space-y-4">
      {/* Add Comment Section */}
      <div className="border border-gray-200 dark:border-gray-700 rounded-lg p-4">
        <h3 className="text-md font-semibold text-gray-900 dark:text-white mb-3">Add Comment</h3>
        <textarea
          value={newComment}
          onChange={(e) => setNewComment(e.target.value)}
          placeholder="Enter your comment here..."
          rows={4}
          className="w-full px-3 py-2 border border-gray-300 rounded-md dark:bg-gray-700 dark:text-white dark:border-gray-600 resize-none"
        />
        <div className="flex justify-end mt-3">
          <Button
            variant="primary"
            onClick={handleSubmit}
            disabled={!newComment.trim() || createMutation.isPending}
            isLoading={createMutation.isPending}
          >
            Add Comment
          </Button>
        </div>
      </div>

      {/* Comments List */}
      <div className="border border-gray-200 dark:border-gray-700 rounded-lg p-4">
        <h3 className="text-md font-semibold text-gray-900 dark:text-white mb-4">
          Comments ({comments.length})
        </h3>

        {isLoading ? (
          <div className="flex justify-center py-8">
            <Loader size="md" />
          </div>
        ) : comments.length === 0 ? (
          <p className="text-sm text-gray-500 dark:text-gray-400 text-center py-8">
            No comments yet. Be the first to comment!
          </p>
        ) : (
          <div className="space-y-4">
            {comments.map((comment) => (
              <div
                key={comment.id}
                className="border-b border-gray-200 dark:border-gray-700 pb-4 last:border-b-0 last:pb-0"
              >
                <div className="flex justify-between items-start mb-2">
                  <div>
                    <p className="text-sm font-medium text-gray-900 dark:text-white">
                      {comment.authorName || comment.authorId}
                    </p>
                    <p className="text-xs text-gray-500 dark:text-gray-400">
                      {new Date(comment.createdAt).toLocaleString()}
                    </p>
                  </div>
                  {user?.id === comment.authorId && (
                    <button
                      onClick={() => handleDelete(comment.id)}
                      className="text-red-600 hover:text-red-900 dark:text-red-400 dark:hover:text-red-300 text-sm"
                      disabled={deleteMutation.isPending}
                    >
                      Delete
                    </button>
                  )}
                </div>
                <p className="text-sm text-gray-700 dark:text-gray-300 whitespace-pre-wrap">
                  {comment.commentText}
                </p>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

