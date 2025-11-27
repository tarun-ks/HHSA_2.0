import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { Modal } from '../../../components/molecules/Modal';
import { Button } from '../../../components/atoms/Button';
import { Input } from '../../../components/atoms/Input';
import { Loader } from '../../../components/atoms/Loader';
import { useToast } from '../../../hooks/useToast';

interface User {
  id: string;
  username: string;
  email: string;
  firstName?: string;
  lastName?: string;
}

interface UserSelectionModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSelect: (userId: string) => void;
  title?: string;
  excludeUserIds?: string[];
}

/**
 * Reusable modal for selecting users.
 * Used for task assignment, reassignment, etc.
 */
export const UserSelectionModal = ({
  isOpen,
  onClose,
  onSelect,
  title = "Select User",
  excludeUserIds = [],
}: UserSelectionModalProps) => {
  const toast = useToast();
  const [searchTerm, setSearchTerm] = useState('');

  // TODO: Replace with actual user service when available
  // For now, using a placeholder - in production, this would call auth-service or user-service
  const { data: usersData, isLoading } = useQuery({
    queryKey: ['users', searchTerm],
    queryFn: async () => {
      // Placeholder - replace with actual API call
      // const response = await userService.getUsers({ search: searchTerm });
      // return response.data;
      return [] as User[];
    },
    enabled: isOpen,
  });

  const users = (usersData || []).filter(
    (user) => !excludeUserIds.includes(user.id)
  );

  const filteredUsers = users.filter((user) => {
    const searchLower = searchTerm.toLowerCase();
    return (
      user.username.toLowerCase().includes(searchLower) ||
      user.email.toLowerCase().includes(searchLower) ||
      (user.firstName && user.firstName.toLowerCase().includes(searchLower)) ||
      (user.lastName && user.lastName.toLowerCase().includes(searchLower))
    );
  });

  const handleSelect = (userId: string) => {
    onSelect(userId);
    onClose();
  };

  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      title={title}
      size="md"
      footer={
        <Button variant="outline" onClick={onClose}>
          Cancel
        </Button>
      }
    >
      <div className="space-y-4">
        <Input
          label="Search Users"
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          placeholder="Search by name, username, or email"
        />

        {isLoading ? (
          <div className="flex justify-center py-8">
            <Loader size="md" />
          </div>
        ) : filteredUsers.length === 0 ? (
          <div className="text-center py-8 text-gray-500 dark:text-gray-400">
            {searchTerm ? 'No users found' : 'No users available'}
          </div>
        ) : (
          <div className="max-h-96 overflow-y-auto space-y-2">
            {filteredUsers.map((user) => (
              <button
                key={user.id}
                onClick={() => handleSelect(user.id)}
                className="w-full text-left p-3 border rounded-lg hover:bg-gray-50 dark:hover:bg-gray-700 dark:border-gray-600 transition-colors"
              >
                <div className="font-medium text-gray-900 dark:text-white">
                  {user.firstName && user.lastName
                    ? `${user.firstName} ${user.lastName}`
                    : user.username}
                </div>
                <div className="text-sm text-gray-500 dark:text-gray-400">
                  {user.email}
                </div>
              </button>
            ))}
          </div>
        )}
      </div>
    </Modal>
  );
};

