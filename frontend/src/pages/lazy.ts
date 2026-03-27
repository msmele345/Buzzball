import { lazy } from 'react';

export const DashboardPage = lazy(() => import('./DashboardPage').then(m => ({ default: m.DashboardPage })));
export const PlayerProfilePage = lazy(() => import('./PlayerProfilePage').then(m => ({ default: m.PlayerProfilePage })));
export const TeamsOverviewPage = lazy(() => import('./TeamsOverviewPage').then(m => ({ default: m.TeamsOverviewPage })));
export const TeamPage = lazy(() => import('./TeamPage').then(m => ({ default: m.TeamPage })));
