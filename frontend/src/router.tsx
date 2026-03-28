import { Suspense } from 'react';
import { createBrowserRouter } from 'react-router-dom';
import { DashboardPage, PlayerProfilePage, TeamsOverviewPage, TeamPage } from './pages/lazy';

const suspenseFallback = (
  <div className="max-w-5xl mx-auto px-4 py-8">
    <div className="animate-pulse h-64 bg-bg-surface rounded-2xl" role="status" aria-label="Loading page" />
  </div>
);

export const router = createBrowserRouter([
  { path: '/', element: <Suspense fallback={suspenseFallback}><DashboardPage /></Suspense> },
  { path: '/players/:id', element: <Suspense fallback={suspenseFallback}><PlayerProfilePage /></Suspense> },
  { path: '/teams', element: <Suspense fallback={suspenseFallback}><TeamsOverviewPage /></Suspense> },
  { path: '/teams/:id', element: <Suspense fallback={suspenseFallback}><TeamPage /></Suspense> },
]);
