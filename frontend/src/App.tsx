import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { StoreProvider } from './providers/StoreProvider';
import { QueryProvider } from './providers/QueryProvider';
import { ThemeProvider } from './providers/ThemeProvider';
import { ToastContainer } from './components/molecules/Toast';
import { Layout } from './components/organisms/Layout';
import { LoginPage } from './pages/auth/LoginPage';
import { ContractListPage } from './pages/contracts/ContractListPage';
import { ContractCreatePage } from './pages/contracts/ContractCreatePage';
import { ContractDetailPage } from './pages/contracts/ContractDetailPage';
import { ContractConfigurePage } from './pages/contracts/ContractConfigurePage';
import { ContractBudgetPage } from './pages/contracts/ContractBudgetPage';
import { TaskListPage } from './pages/workflows/TaskListPage';
import { ProtectedRoute as AuthProtectedRoute } from './frameworks/authorization';

/**
 * Main App component.
 */
function App() {
  return (
    <StoreProvider>
      <QueryProvider>
        <ThemeProvider>
          <BrowserRouter>
            <Routes>
              <Route path="/login" element={<LoginPage />} />
              <Route
                path="/dashboard"
                element={
                  <AuthProtectedRoute>
                    <Layout>
                      <div>
                        <h1 className="text-2xl font-bold">Dashboard</h1>
                        <p className="mt-4">Welcome to the dashboard!</p>
                      </div>
                    </Layout>
                  </AuthProtectedRoute>
                }
              />
              <Route
                path="/contracts"
                element={
                  <AuthProtectedRoute permission="CONTRACT_VIEW">
                    <Layout>
                      <ContractListPage />
                    </Layout>
                  </AuthProtectedRoute>
                }
              />
              <Route
                path="/contracts/new"
                element={
                  <AuthProtectedRoute permission="CONTRACT_CREATE">
                    <Layout>
                      <ContractCreatePage />
                    </Layout>
                  </AuthProtectedRoute>
                }
              />
              <Route
                path="/contracts/:id"
                element={
                  <AuthProtectedRoute permission="CONTRACT_VIEW">
                    <Layout>
                      <ContractDetailPage />
                    </Layout>
                  </AuthProtectedRoute>
                }
              />
              <Route
                path="/contracts/:id/configure"
                element={
                  <AuthProtectedRoute permission="CONTRACT_CONFIGURE">
                    <Layout>
                      <ContractConfigurePage />
                    </Layout>
                  </AuthProtectedRoute>
                }
              />
              <Route
                path="/contracts/:id/budget"
                element={
                  <AuthProtectedRoute permission="CONTRACT_BUDGET_SAVE">
                    <Layout>
                      <ContractBudgetPage />
                    </Layout>
                  </AuthProtectedRoute>
                }
              />
              <Route
                path="/tasks"
                element={
                  <AuthProtectedRoute permission="WORKFLOW_TASK_VIEW">
                    <Layout>
                      <TaskListPage />
                    </Layout>
                  </AuthProtectedRoute>
                }
              />
              <Route path="/" element={<Navigate to="/dashboard" replace />} />
            </Routes>
            <ToastContainer />
          </BrowserRouter>
        </ThemeProvider>
      </QueryProvider>
    </StoreProvider>
  );
}

export default App;
