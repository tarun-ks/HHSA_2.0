import { Provider } from 'react-redux';
import { store } from '../store/store';
import { ReactNode } from 'react';

interface StoreProviderProps {
  children: ReactNode;
}

/**
 * Redux store provider.
 */
export const StoreProvider = ({ children }: StoreProviderProps) => {
  return <Provider store={store}>{children}</Provider>;
};




