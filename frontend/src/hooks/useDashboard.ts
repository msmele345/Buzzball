import { useQuery } from '@tanstack/react-query';
import { fetchDashboard, fetchLeagueLeaders, fetchTrending } from '../api/dashboard';

export const useDashboard = () =>
  useQuery({
    queryKey: ['dashboard'],
    queryFn: fetchDashboard,
    staleTime: 2 * 60 * 1000,
    refetchInterval: 2 * 60 * 1000,
  });

export const useTrending = (category: 'hitting' | 'pitching' = 'hitting') =>
  useQuery({
    queryKey: ['trending', category],
    queryFn: () => fetchTrending(category),
    staleTime: 2 * 60 * 1000,
  });

export const useLeagueLeaders = () =>
  useQuery({
    queryKey: ['league-leaders'],
    queryFn: fetchLeagueLeaders,
    staleTime: 5 * 60 * 1000,
  });
