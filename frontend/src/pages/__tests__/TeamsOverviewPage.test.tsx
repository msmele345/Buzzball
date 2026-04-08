import { render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { TeamsOverviewPage } from '../TeamsOverviewPage.tsx';
import type { TeamSummaryDto } from '../../types';

vi.mock('../../hooks/useTeams');

import { useTeams } from '../../hooks/useTeams.ts';

const mockUseTeams = vi.mocked(useTeams);

const mockAlTeam: TeamSummaryDto = {
  teamId: 'nyy',
  name: 'New York Yankees',
  abbreviation: 'NYY',
  division: 'AL East',
  league: 'AL',
  wins: 95,
  losses: 67,
  winPct: 0.586,
  gamesBack: 0,
  runDifferential: 120,
};

const mockAlTeam2: TeamSummaryDto = {
  teamId: 'bos',
  name: 'Boston Red Sox',
  abbreviation: 'BOS',
  division: 'AL East',
  league: 'AL',
  wins: 82,
  losses: 80,
  winPct: 0.506,
  gamesBack: 13,
  runDifferential: -12,
};

const mockNlTeam: TeamSummaryDto = {
  teamId: 'nym',
  name: 'New York Mets',
  abbreviation: 'NYM',
  division: 'NL East',
  league: 'NL',
  wins: 89,
  losses: 73,
  winPct: 0.549,
  gamesBack: 3,
  runDifferential: 45,
};

function renderPage() {
  return render(
    <MemoryRouter>
      <TeamsOverviewPage />
    </MemoryRouter>
  );
}

const mockQueryResult = (overrides: Record<string, unknown> = {}) => {
  return { data: undefined, isLoading: false, error: null, ...overrides } as ReturnType<typeof useTeams>;
}

beforeEach(() => {
  vi.clearAllMocks();
  mockUseTeams.mockReturnValue(mockQueryResult());
});

describe('TeamsOverviewPage', () => {
  describe('given teams are loading', () => {
    it('should display a loading skeleton', () => {
      mockUseTeams.mockReturnValue(mockQueryResult({ isLoading: true }));

      renderPage();

      expect(screen.getByRole('status', { name: /loading teams/i })).toBeInTheDocument();
    });

    it('should render six skeleton rows', () => {
      mockUseTeams.mockReturnValue(mockQueryResult({ isLoading: true }));

      renderPage();

      const skeleton = screen.getByRole('status', { name: /loading teams/i });
      const rows = skeleton.querySelectorAll('.animate-pulse');
      expect(rows).toHaveLength(6);
    });

    it('should not display the page heading', () => {
      mockUseTeams.mockReturnValue(mockQueryResult({ isLoading: true }));

      renderPage();

      expect(screen.queryByRole('heading')).not.toBeInTheDocument();
    });
  });

  describe('given the fetch returns an error', () => {
    it('should display an error message', () => {
      mockUseTeams.mockReturnValue(mockQueryResult({ error: new Error('Network error') }));

      renderPage();

      expect(screen.getByText('Bobbled at short')).toBeInTheDocument();
    });

    it('should not display any team cards', () => {
      mockUseTeams.mockReturnValue(mockQueryResult({ error: new Error('fail') }));

      renderPage();

      expect(screen.queryByText('NYY')).not.toBeInTheDocument();
    });
  });

  describe('given teams are loaded successfully', () => {
    const allTeams = [mockAlTeam, mockAlTeam2, mockNlTeam];

    beforeEach(() => {
      mockUseTeams.mockReturnValue(mockQueryResult({ data: allTeams }));
    });

    it('should display the page heading', () => {
      renderPage();

      expect(screen.getByRole('heading', { level: 1 })).toHaveTextContent('Teams');
    });

    it('should display the American League section', () => {
      renderPage();

      expect(screen.getByText('American League')).toBeInTheDocument();
    });

    it('should display the National League section', () => {
      renderPage();

      expect(screen.getByText('National League')).toBeInTheDocument();
    });

    it('should display AL team abbreviations', () => {
      renderPage();

      expect(screen.getByText('NYY')).toBeInTheDocument();
      expect(screen.getByText('BOS')).toBeInTheDocument();
    });

    it('should display NL team abbreviations', () => {
      renderPage();

      expect(screen.getByText('NYM')).toBeInTheDocument();
    });

    it('should display the win-loss record for each team', () => {
      renderPage();

      expect(screen.getByText('95-67')).toBeInTheDocument();
      expect(screen.getByText('82-80')).toBeInTheDocument();
      expect(screen.getByText('89-73')).toBeInTheDocument();
    });

    it('should link each team card to its team page', () => {
      renderPage();

      const nyyLink = screen.getByText('NYY').closest('a');
      expect(nyyLink).toHaveAttribute('href', '/teams/nyy');

      const bosLink = screen.getByText('BOS').closest('a');
      expect(bosLink).toHaveAttribute('href', '/teams/bos');

      const nymLink = screen.getByText('NYM').closest('a');
      expect(nymLink).toHaveAttribute('href', '/teams/nym');
    });
  });

  describe('given teams data is an empty array', () => {
    it('should display the page heading with no team cards', () => {
      mockUseTeams.mockReturnValue(mockQueryResult({ data: [] }));

      renderPage();

      expect(screen.getByRole('heading', { level: 1 })).toHaveTextContent('Teams');
      expect(screen.getByText('American League')).toBeInTheDocument();
      expect(screen.getByText('National League')).toBeInTheDocument();
      expect(screen.queryByRole('link')).not.toBeInTheDocument();
    });
  });
});
