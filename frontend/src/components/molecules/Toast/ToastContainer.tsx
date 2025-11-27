import { useAppSelector } from '../../../store/hooks';
import { Toast } from './Toast';

/**
 * Toast container molecule component.
 * Renders all active toast notifications.
 */
export const ToastContainer = () => {
  const notifications = useAppSelector((state) => state.notifications.notifications);

  if (notifications.length === 0) {
    return null;
  }

  return (
    <div className="fixed top-4 right-4 z-50 flex flex-col items-end">
      {notifications.map((notification) => (
        <Toast key={notification.id} notification={notification} />
      ))}
    </div>
  );
};




