import { useEffect } from 'react';
import { useAppDispatch } from '../../../store/hooks';
import { removeNotification, Notification } from '../../../store/slices/notificationSlice';
import { cn } from '../../../utils/cn';

interface ToastProps {
  notification: Notification;
}

/**
 * Toast molecule component.
 * Displays a notification toast with auto-dismiss.
 */
export const Toast = ({ notification }: ToastProps) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    if (notification.duration && notification.duration > 0) {
      const timer = setTimeout(() => {
        dispatch(removeNotification(notification.id));
      }, notification.duration);

      return () => clearTimeout(timer);
    }
  }, [notification.id, notification.duration, dispatch]);

  const handleClose = () => {
    dispatch(removeNotification(notification.id));
  };

  const typeStyles = {
    success: 'bg-green-50 border-green-200 text-green-800 dark:bg-green-900/20 dark:border-green-800 dark:text-green-200',
    error: 'bg-red-50 border-red-200 text-red-800 dark:bg-red-900/20 dark:border-red-800 dark:text-red-200',
    warning: 'bg-yellow-50 border-yellow-200 text-yellow-800 dark:bg-yellow-900/20 dark:border-yellow-800 dark:text-yellow-200',
    info: 'bg-blue-50 border-blue-200 text-blue-800 dark:bg-blue-900/20 dark:border-blue-800 dark:text-blue-200',
  };

  return (
    <div
      className={cn(
        'flex items-start p-4 mb-2 border rounded-lg shadow-lg max-w-md',
        'animate-in slide-in-from-top-5 fade-in',
        typeStyles[notification.type]
      )}
      role="alert"
    >
      <div className="flex-1">
        {notification.title && (
          <h4 className="font-semibold mb-1">{notification.title}</h4>
        )}
        <p className="text-sm">{notification.message}</p>
      </div>
      <button
        onClick={handleClose}
        className="ml-4 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300"
        aria-label="Close"
      >
        <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
        </svg>
      </button>
    </div>
  );
};




