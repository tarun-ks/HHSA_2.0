import { useAppDispatch } from '../store/hooks';
import { addNotification, NotificationType } from '../store/slices/notificationSlice';

/**
 * Hook for showing toast notifications.
 */
export const useToast = () => {
  const dispatch = useAppDispatch();

  const showToast = (
    type: NotificationType,
    message: string,
    options?: {
      title?: string;
      duration?: number;
    }
  ) => {
    dispatch(
      addNotification({
        type,
        message,
        title: options?.title,
        duration: options?.duration,
      })
    );
  };

  return {
    success: (message: string, options?: { title?: string; duration?: number }) =>
      showToast('success', message, options),
    error: (message: string, options?: { title?: string; duration?: number }) =>
      showToast('error', message, options),
    warning: (message: string, options?: { title?: string; duration?: number }) =>
      showToast('warning', message, options),
    info: (message: string, options?: { title?: string; duration?: number }) =>
      showToast('info', message, options),
  };
};




