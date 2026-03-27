import { useDashboard } from '../../hooks/useDashboard';
import type { TrendingPlayerDto } from '../../types';
import { Link } from 'react-router-dom';
import clsx from 'clsx';

function TrendingCard({ player }: { player: TrendingPlayerDto }) {
  const warDelta = player.warDelta7d;
  return (
    <Link
      to={`/players/${player.playerId}`}
      className="block p-4 bg-bg-surface border border-border rounded-xl transition-all duration-200 hover:bg-bg-hover hover:border-neon-green/25 hover:scale-[1.02] hover:shadow-[0_0_15px_rgba(0,255,135,0.1)]"
    >
      <div className="flex items-center justify-between mb-2">
        <span className="font-semibold text-white text-sm">{player.name}</span>
        <span className="text-xs text-text-secondary">{player.position}</span>
      </div>
      <div className="flex items-center justify-between">
        <span className="text-xs text-text-muted">{player.teamId}</span>
        {player.currentWar != null && (
          <span
            className={clsx('text-xs font-mono font-medium', {
              'text-neon-green': warDelta > 0,
              'text-hot-red': warDelta < 0,
              'text-text-secondary': warDelta === 0,
            })}
          >
            WAR: {player.currentWar.toFixed(1)}
            {warDelta !== 0 && ` (${warDelta > 0 ? '+' : ''}${warDelta.toFixed(2)})`}
          </span>
        )}
      </div>
    </Link>
  );
}

export function TrendingPlayersSection() {
  const { data: dashboard, isLoading, error } = useDashboard();

  if (isLoading) return (
    <div className="space-y-3">
      {[...Array(3)].map((_, i) => (
        <div key={i} className="animate-pulse h-16 bg-bg-surface rounded-xl" />
      ))}
    </div>
  );
  if (error) return <div className="text-hot-red text-sm">Failed to load trending players</div>;
  if (!dashboard) return null;

  return (
    <section>
      <h2 className="text-xl font-bold text-white mb-4 border-l-2 border-neon-green pl-3">Trending Players</h2>
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div>
          <h3 className="text-xs font-semibold text-text-muted uppercase tracking-[0.15em] mb-3">Hitters</h3>
          <div className="space-y-2">
            {dashboard.trendingHitters.slice(0, 5).map((player) => (
              <TrendingCard key={player.playerId} player={player} />
            ))}
          </div>
        </div>
        <div>
          <h3 className="text-xs font-semibold text-text-muted uppercase tracking-[0.15em] mb-3">Pitchers</h3>
          <div className="space-y-2">
            {dashboard.trendingPitchers.slice(0, 5).map((player) => (
              <TrendingCard key={player.playerId} player={player} />
            ))}
          </div>
        </div>
      </div>
    </section>
  );
}
