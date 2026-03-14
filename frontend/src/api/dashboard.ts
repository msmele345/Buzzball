import apiClient from './client';
import type { DashboardDto, TrendingPlayerDto, LeagueLeaderDto } from '../types';

export const fetchDashboard = async (): Promise<DashboardDto> => {
  const { data } = await apiClient.get<DashboardDto>('/dashboard');
  return data;
};

export const fetchTrending = async (category: 'hitting' | 'pitching' = 'hitting'): Promise<TrendingPlayerDto[]> => {
  const { data } = await apiClient.get<TrendingPlayerDto[]>('/dashboard/trending', {
    params: { category },
  });
  return data;
};

export const fetchLeagueLeaders = async (): Promise<LeagueLeaderDto[]> => {
  const { data } = await apiClient.get<LeagueLeaderDto[]>('/dashboard/league-leaders');
  return data;
};
