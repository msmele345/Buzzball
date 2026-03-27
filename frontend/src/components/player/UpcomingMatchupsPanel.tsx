import { useUpcomingMatchups } from '../../hooks/usePlayers';

function wobaColor(woba: number): string {
  if (woba >= 0.370) return 'text-neon-green';
  if (woba >= 0.320) return 'text-gold';
  return 'text-hot-red';
}

function wobaLabel(woba: number): string {
  if (woba >= 0.370) return 'Excellent';
  if (woba >= 0.340) return 'Above Avg';
  if (woba >= 0.310) return 'Average';
  return 'Below Avg';
}

export function UpcomingMatchupsPanel({ playerId }: { playerId: string }) {
  const { data: matchups, isLoading, error } = useUpcomingMatchups(playerId);

  let content: React.ReactNode;

  if (isLoading) {
    content = (
      <div className="space-y-3" role="status" aria-label="Loading matchups">
        {[...Array(3)].map((_, i) => (
          <div key={i} className="animate-pulse h-12 bg-bg-surface-alt rounded" />
        ))}
      </div>
    );
  } else if (error) {
    content = <div className="text-text-muted text-sm">Projections unavailable</div>;
  } else if (!matchups?.length) {
    content = <div className="text-text-muted text-sm">No upcoming matchup projections</div>;
  } else {
    content = (
      <div className="space-y-3">
        {matchups.map((matchup) => (
          <div
            key={matchup.id}
            className="flex items-center justify-between py-2 border-b border-bg-surface-alt last:border-0"
          >
            <div>
              <p className="text-white text-sm font-medium">{matchup.gameDate}</p>
              <p className="text-text-secondary text-xs">
                vs Pitcher #{matchup.opposingPitcherId}
                {matchup.handedness && ` (${matchup.handedness}HP)`}
                {matchup.venue && ` · ${matchup.venue}`}
              </p>
              {matchup.parkFactor !== 1.0 && (
                <p className="text-text-muted text-xs">
                  Park factor: {matchup.parkFactor.toFixed(2)}
                </p>
              )}
            </div>
            <div className="text-right">
              <p className={`text-lg font-bold font-mono ${wobaColor(matchup.projectedWoba)}`}>
                {matchup.projectedWoba.toFixed(3)}
              </p>
              <p className="text-xs text-text-muted">proj. wOBA</p>
              <p className={`text-xs ${wobaColor(matchup.projectedWoba)}`}>
                {wobaLabel(matchup.projectedWoba)}
              </p>
            </div>
          </div>
        ))}
      </div>
    );
  }

  return (
    <div className="bg-bg-surface border border-border rounded-xl p-6">
      <h3 className="text-xs font-semibold text-text-muted uppercase tracking-[0.15em] mb-3">
        Upcoming Matchups
      </h3>
      {content}
    </div>
  );
}
