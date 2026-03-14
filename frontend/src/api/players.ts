import apiClient from './client';
import type { PlayerSummaryDto, PlayerDetailDto, BattingStatsDto, PitchingStatsDto, FieldingStatsDto } from '../types';

export const fetchPlayers = async (): Promise<PlayerSummaryDto[]> => {
  const { data } = await apiClient.get<PlayerSummaryDto[]>('/players');
  return data;
};

export const fetchPlayer = async (id: string): Promise<PlayerDetailDto> => {
  const { data } = await apiClient.get<PlayerDetailDto>(`/players/${id}`);
  return data;
};

export const fetchBattingStats = async (playerId: string): Promise<BattingStatsDto[]> => {
  const { data } = await apiClient.get<BattingStatsDto[]>(`/players/${playerId}/batting`);
  return data;
};

export const fetchPitchingStats = async (playerId: string): Promise<PitchingStatsDto[]> => {
  const { data } = await apiClient.get<PitchingStatsDto[]>(`/players/${playerId}/pitching`);
  return data;
};

export const fetchFieldingStats = async (playerId: string): Promise<FieldingStatsDto[]> => {
  const { data } = await apiClient.get<FieldingStatsDto[]>(`/players/${playerId}/fielding`);
  return data;
};
