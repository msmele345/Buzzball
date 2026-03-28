import { Component, type ReactNode } from 'react';
import { RouterProvider } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { router } from './router';
import { useLiveUpdates } from './hooks/useLiveUpdates';

class ErrorBoundary extends Component<
  { children: ReactNode },
  { hasError: boolean }
> {
  state = { hasError: false };

  static getDerivedStateFromError() {
    return { hasError: true };
  }

  render() {
    if (this.state.hasError) {
      return (
        <div className="min-h-screen bg-bg-primary flex items-center justify-center p-8">
          <div className="bg-bg-surface border border-border rounded-2xl p-8 max-w-md text-center">
            <h1 className="text-xl font-bold text-white mb-2">Something went wrong</h1>
            <p className="text-text-secondary mb-6">An unexpected error occurred. Please reload the page.</p>
            <button
              onClick={() => window.location.reload()}
              className="px-4 py-2 bg-neon-green text-black font-semibold rounded-lg hover:opacity-90 transition-opacity"
            >
              Reload
            </button>
          </div>
        </div>
      );
    }
    return this.props.children;
  }
}

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      retry: 1,
      refetchOnWindowFocus: false,
    },
  },
});

function AppShell() {
  useLiveUpdates();
  return (
    <div className="min-h-screen bg-bg-primary text-white font-sans">
      <RouterProvider router={router} />
    </div>
  );
}

export default function App() {
  return (
    <ErrorBoundary>
      <QueryClientProvider client={queryClient}>
        <AppShell />
      </QueryClientProvider>
    </ErrorBoundary>
  );
}
