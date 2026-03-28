import { useQuery } from '@tanstack/react-query';
import { fetchPlayer, fetchPlayers, fetchBattingStats, fetchPitchingStats, fetchFieldingStats, fetchUpcomingMatchups } from '../api/players';

export const usePlayers = () =>
  useQuery({
    queryKey: ['players'],
    queryFn: fetchPlayers,
    staleTime: 5 * 60 * 1000,
  });

export const usePlayer = (id: string) =>
  useQuery({
    queryKey: ['players', id],
    queryFn: () => fetchPlayer(id),
    enabled: Boolean(id),
    staleTime: 5 * 60 * 1000,
  });

export const useBattingStats = (playerId: string) =>
  useQuery({
    queryKey: ['batting-stats', playerId],
    queryFn: () => fetchBattingStats(playerId),
    enabled: Boolean(playerId),
    staleTime: 5 * 60 * 1000,
  });

export const usePitchingStats = (playerId: string) =>
  useQuery({
    queryKey: ['pitching-stats', playerId],
    queryFn: () => fetchPitchingStats(playerId),
    enabled: Boolean(playerId),
    staleTime: 5 * 60 * 1000,
  });

export const useFieldingStats = (playerId: string) =>
  useQuery({
    queryKey: ['fielding-stats', playerId],
    queryFn: () => fetchFieldingStats(playerId),
    enabled: Boolean(playerId),
    staleTime: 5 * 60 * 1000,
  });

export const useUpcomingMatchups = (playerId: string) =>
  useQuery({
    queryKey: ['matchups', playerId],
    queryFn: () => fetchUpcomingMatchups(playerId),
    enabled: Boolean(playerId),
    staleTime: 60 * 60 * 1000,
  });
