import { useQuery } from '@tanstack/react-query';
import apiClient from '../../api/client';

interface MatchupProjection {
  id: string;
  playerId: string;
  opposingPitcherId: string;
  gameDate: string;
  venue: string;
  projectedWoba: number;
  handedness: string;
  parkFactor: number;
}

function fetchUpcomingMatchups(playerId: string): Promise<MatchupProjection[]> {
  return apiClient.get<MatchupProjection[]>(`/matchups/${playerId}/upcoming`)
    .then(r => r.data);
}

function wobaColor(woba: number): string {
  if (woba >= 0.370) return 'text-green-400';
  if (woba >= 0.320) return 'text-yellow-400';
  return 'text-red-400';
}

function wobaLabel(woba: number): string {
  if (woba >= 0.370) return 'Excellent';
  if (woba >= 0.340) return 'Above Avg';
  if (woba >= 0.310) return 'Average';
  return 'Below Avg';
}

export function UpcomingMatchupsPanel({ playerId }: { playerId: string }) {
  const { data: matchups, isLoading, error } = useQuery({
    queryKey: ['matchups', playerId],
    queryFn: () => fetchUpcomingMatchups(playerId),
    enabled: Boolean(playerId),
    staleTime: 60 * 60 * 1000, // 1 hour — projections have 24hr TTL on backend
  });

  if (isLoading) {
    return (
      <div className="bg-gray-800 rounded-lg p-6">
        <h3 className="text-sm font-semibold text-gray-400 uppercase tracking-wide mb-3">
          Upcoming Matchups
        </h3>
        <div className="text-gray-500 text-sm">Loading projections...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="bg-gray-800 rounded-lg p-6">
        <h3 className="text-sm font-semibold text-gray-400 uppercase tracking-wide mb-3">
          Upcoming Matchups
        </h3>
        <div className="text-gray-500 text-sm">Projections unavailable</div>
      </div>
    );
  }

  if (!matchups?.length) {
    return (
      <div className="bg-gray-800 rounded-lg p-6">
        <h3 className="text-sm font-semibold text-gray-400 uppercase tracking-wide mb-3">
          Upcoming Matchups
        </h3>
        <div className="text-gray-500 text-sm">No upcoming matchup projections</div>
      </div>
    );
  }

  return (
    <div className="bg-gray-800 rounded-lg p-6">
      <h3 className="text-sm font-semibold text-gray-400 uppercase tracking-wide mb-4">
        Upcoming Matchups
      </h3>
      <div className="space-y-3">
        {matchups.map((matchup) => (
          <div
            key={matchup.id}
            className="flex items-center justify-between py-2 border-b border-gray-700 last:border-0"
          >
            <div>
              <p className="text-white text-sm font-medium">{matchup.gameDate}</p>
              <p className="text-gray-400 text-xs">
                vs Pitcher #{matchup.opposingPitcherId}
                {matchup.handedness && ` (${matchup.handedness}HP)`}
                {matchup.venue && ` · ${matchup.venue}`}
              </p>
              {matchup.parkFactor !== 1.0 && (
                <p className="text-gray-600 text-xs">
                  Park factor: {matchup.parkFactor.toFixed(2)}
                </p>
              )}
            </div>
            <div className="text-right">
              <p className={`text-lg font-bold ${wobaColor(matchup.projectedWoba)}`}>
                {matchup.projectedWoba.toFixed(3)}
              </p>
              <p className="text-xs text-gray-500">proj. wOBA</p>
              <p className={`text-xs ${wobaColor(matchup.projectedWoba)}`}>
                {wobaLabel(matchup.projectedWoba)}
              </p>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
