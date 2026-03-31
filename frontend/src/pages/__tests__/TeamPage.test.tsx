import { render, screen } from '@testing-library/react';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { TeamPage } from '../TeamPage.tsx';
import type { TeamSummaryDto, RosterPlayerDto } from '../../types';

vi.mock('../../hooks/useTeams');

import { useTeam, useTeamRoster } from '../../hooks/useTeams.ts';

const mockUseTeam = vi.mocked(useTeam);
const mockUseTeamRoster = vi.mocked(useTeamRoster);

const mockTeam: TeamSummaryDto = {
  teamId: 'team-1',
  name: 'New York Yankees',
  abbreviation: 'NYY',
  division: 'AL East',
  league: 'American League',
  wins: 95,
  losses: 67,
  winPct: 0.586,
  gamesBack: 0,
  runDifferential: 120,
};

const mockRoster: RosterPlayerDto[] = [
  { playerId: 'p1', name: 'Aaron Judge', position: 'RF', jerseyNumber: '99' },
  { playerId: 'p2', name: 'Juan Soto', position: 'LF', jerseyNumber: '22' },
];

function renderTeamPage(teamId = 'team-1') {
  return render(
    <MemoryRouter initialEntries={[`/teams/${teamId}`]}>
      <Routes>
        <Route path="/teams/:id" element={<TeamPage />} />
      </Routes>
    </MemoryRouter>
  );
}

function mockQueryResult(overrides: Record<string, unknown> = {}) {
  return { data: undefined, isLoading: false, error: null, ...overrides } as ReturnType<typeof useTeam>;
}

beforeEach(() => {
  vi.clearAllMocks();
  mockUseTeam.mockReturnValue(mockQueryResult());
  mockUseTeamRoster.mockReturnValue(mockQueryResult() as ReturnType<typeof useTeamRoster>);
});

describe('TeamPage', () => {
  describe('given the team data is loading', () => {
    it('should display a loading skeleton', () => {
      mockUseTeam.mockReturnValue(mockQueryResult({ isLoading: true }));

      renderTeamPage();

      expect(screen.getByRole('status', { name: /loading team/i })).toBeInTheDocument();
    });

    it('should not display team details', () => {
      mockUseTeam.mockReturnValue(mockQueryResult({ isLoading: true }));

      renderTeamPage();

      expect(screen.queryByRole('heading')).not.toBeInTheDocument();
    });
  });

  describe('given the team fetch returns an error', () => {
    it('should display an error message', () => {
      mockUseTeam.mockReturnValue(mockQueryResult({ error: new Error('Not found') }));

      renderTeamPage();

      expect(screen.getByText('Team not found')).toBeInTheDocument();
    });
  });

  describe('given the team data is loaded successfully', () => {
    beforeEach(() => {
      mockUseTeam.mockReturnValue(mockQueryResult({ data: mockTeam }));
    });

    it('should display the team name', () => {
      renderTeamPage();

      expect(screen.getByRole('heading', { level: 1 })).toHaveTextContent('New York Yankees');
    });

    it('should display the division and league', () => {
      renderTeamPage();

      expect(screen.getByText(/AL East · American League/)).toBeInTheDocument();
    });

    it('should display the win-loss record', () => {
      renderTeamPage();

      expect(screen.getByText('95-67')).toBeInTheDocument();
      expect(screen.getByText('Record')).toBeInTheDocument();
    });

    it('should display the win percentage', () => {
      renderTeamPage();

      expect(screen.getByText('0.586')).toBeInTheDocument();
      expect(screen.getByText('PCT')).toBeInTheDocument();
    });

    it('should display a positive run differential with a plus sign', () => {
      renderTeamPage();

      expect(screen.getByText('+120')).toBeInTheDocument();
      expect(screen.getByText('Run Diff')).toBeInTheDocument();
    });

    it('should style a positive run differential in green', () => {
      renderTeamPage();

      const runDiff = screen.getByText('+120');
      expect(runDiff).toHaveClass('text-neon-green');
    });

    it('should style a negative run differential in red', () => {
      const losingTeam = { ...mockTeam, runDifferential: -45 };
      mockUseTeam.mockReturnValue(mockQueryResult({ data: losingTeam }));

      renderTeamPage();

      const runDiff = screen.getByText('-45');
      expect(runDiff).toHaveClass('text-hot-red');
    });

    it('should not show a plus sign for zero run differential', () => {
      const evenTeam = { ...mockTeam, runDifferential: 0 };
      mockUseTeam.mockReturnValue(mockQueryResult({ data: evenTeam }));

      renderTeamPage();

      expect(screen.getByText('0')).toBeInTheDocument();
    });
  });

  describe('given a roster is available', () => {
    beforeEach(() => {
      mockUseTeam.mockReturnValue(mockQueryResult({ data: mockTeam }));
      mockUseTeamRoster.mockReturnValue(mockQueryResult({ data: mockRoster }) as ReturnType<typeof useTeamRoster>);
    });

    it('should display the roster heading', () => {
      renderTeamPage();

      expect(screen.getByRole('heading', { level: 2 })).toHaveTextContent('Roster');
    });

    it('should display each player name and position', () => {
      renderTeamPage();

      expect(screen.getByText('Aaron Judge')).toBeInTheDocument();
      expect(screen.getByText('RF')).toBeInTheDocument();
      expect(screen.getByText('Juan Soto')).toBeInTheDocument();
      expect(screen.getByText('LF')).toBeInTheDocument();
    });

    it('should link each player to their player page', () => {
      renderTeamPage();

      const judgeLink = screen.getByText('Aaron Judge').closest('a');
      expect(judgeLink).toHaveAttribute('href', '/players/p1');

      const sotoLink = screen.getByText('Juan Soto').closest('a');
      expect(sotoLink).toHaveAttribute('href', '/players/p2');
    });
  });

  describe('given no roster is available', () => {
    beforeEach(() => {
      mockUseTeam.mockReturnValue(mockQueryResult({ data: mockTeam }));
    });

    it('should not display the roster section when roster is undefined', () => {
      mockUseTeamRoster.mockReturnValue(mockQueryResult({ data: undefined }) as ReturnType<typeof useTeamRoster>);

      renderTeamPage();

      expect(screen.queryByText('Roster')).not.toBeInTheDocument();
    });

    it('should not display the roster section when roster is empty', () => {
      mockUseTeamRoster.mockReturnValue(mockQueryResult({ data: [] }) as ReturnType<typeof useTeamRoster>);

      renderTeamPage();

      expect(screen.queryByText('Roster')).not.toBeInTheDocument();
    });
  });
});
