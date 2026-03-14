import { createBrowserRouter } from 'react-router-dom';
import { DashboardPage } from './pages/DashboardPage';
import { PlayerProfilePage } from './pages/PlayerProfilePage';
import { TeamsOverviewPage } from './pages/TeamsOverviewPage';
import { TeamPage } from './pages/TeamPage';

export const router = createBrowserRouter([
  { path: '/', element: <DashboardPage /> },
  { path: '/players/:id', element: <PlayerProfilePage /> },
  { path: '/teams', element: <TeamsOverviewPage /> },
  { path: '/teams/:id', element: <TeamPage /> },
]);
