import { useQuery } from '@tanstack/react-query';
import { fetchTeam, fetchTeamComparison, fetchTeamRoster, fetchTeams } from '../api/teams';

export const useTeams = () =>
  useQuery({
    queryKey: ['teams'],
    queryFn: fetchTeams,
    staleTime: 5 * 60 * 1000,
  });

export const useTeam = (id: string) =>
  useQuery({
    queryKey: ['teams', id],
    queryFn: () => fetchTeam(id),
    enabled: Boolean(id),
    staleTime: 5 * 60 * 1000,
  });

export const useTeamRoster = (teamId: string) =>
  useQuery({
    queryKey: ['team-roster', teamId],
    queryFn: () => fetchTeamRoster(teamId),
    enabled: Boolean(teamId),
    staleTime: 5 * 60 * 1000,
  });

export const useTeamComparison = (teamIds: string[]) =>
  useQuery({
    queryKey: ['team-comparison', teamIds],
    queryFn: () => fetchTeamComparison(teamIds),
    enabled: teamIds.length >= 2,
    staleTime: 5 * 60 * 1000,
  });
