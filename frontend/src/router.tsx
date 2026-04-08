import { Suspense } from 'react';
import { createBrowserRouter, Outlet } from 'react-router-dom';
import { DashboardPage, PlayerProfilePage, TeamsOverviewPage, TeamPage } from './pages/lazy';
import { TopNav } from './components/nav/TopNav';

const suspenseFallback = (
  <div className="max-w-5xl mx-auto px-4 py-8">
    <div className="animate-pulse h-64 bg-bg-surface rounded-2xl" role="status" aria-label="Loading page" />
  </div>
);

function RootLayout() {
  return (
    <>
      <TopNav />
      <main className="flex-1">
        <Outlet />
      </main>
    </>
  );
}

export const router = createBrowserRouter([
  {
    element: <RootLayout />,
    children: [
      { path: '/', element: <Suspense fallback={suspenseFallback}><DashboardPage /></Suspense> },
      { path: '/players/:id', element: <Suspense fallback={suspenseFallback}><PlayerProfilePage /></Suspense> },
      { path: '/teams', element: <Suspense fallback={suspenseFallback}><TeamsOverviewPage /></Suspense> },
      { path: '/teams/:id', element: <Suspense fallback={suspenseFallback}><TeamPage /></Suspense> },
    ],
  },
]);
