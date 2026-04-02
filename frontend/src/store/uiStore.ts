import { create } from 'zustand';

type League = 'AL' | 'NL' | 'all';

interface UiState {
  selectedSeason: number;
  leagueFilter: League;
  teamComparisonList: string[];
  // setTeamComparisonList?: (list: string[]) => void;
  setSelectedSeason: (season: number) => void;
  setLeagueFilter: (league: League) => void;
  addTeamToComparison: (teamId: string) => void;
  removeTeamFromComparison: (teamId: string) => void;
  clearComparison: () => void;
}

export const useUiStore = create<UiState>((set) => ({
  selectedSeason: new Date().getFullYear(),
  leagueFilter: 'all',
  teamComparisonList: [],
  // setTeamComparisonList: (teams: string[]) => set({ teamComparisonList: teams }),
  setSelectedSeason: (season) => set({ selectedSeason: season }),
  setLeagueFilter: (league) => set({ leagueFilter: league }),
  addTeamToComparison: (teamId) =>
    set((state) => ({
      teamComparisonList:
        state.teamComparisonList.includes(teamId) || state.teamComparisonList.length >= 3
          ? state.teamComparisonList
          : [...state.teamComparisonList, teamId],
    })),
  removeTeamFromComparison: (teamId) =>
    set((state) => ({
      teamComparisonList: state.teamComparisonList.filter((id) => id !== teamId),
    })),
  clearComparison: () => set({ teamComparisonList: [] }),
}));
