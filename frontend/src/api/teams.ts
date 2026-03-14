import apiClient from './client';
import type { TeamSummaryDto, TeamComparisonDto } from '../types';

export const fetchTeams = async (): Promise<TeamSummaryDto[]> => {
  const { data } = await apiClient.get<TeamSummaryDto[]>('/teams');
  return data;
};

export const fetchTeam = async (id: string): Promise<TeamSummaryDto> => {
  const { data } = await apiClient.get<TeamSummaryDto>(`/teams/${id}`);
  return data;
};

export const fetchTeamRoster = async (teamId: string) => {
  const { data } = await apiClient.get(`/teams/${teamId}/roster`);
  return data;
};

export const fetchTeamComparison = async (teamIds: string[]): Promise<TeamComparisonDto> => {
  const { data } = await apiClient.get<TeamComparisonDto>('/teams/compare', {
    params: { teamIds: teamIds.join(',') },
  });
  return data;
};
