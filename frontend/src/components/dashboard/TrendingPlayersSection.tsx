import { useDashboard } from '../../hooks/useDashboard';
import type { TrendingPlayerDto } from '../../types';
import { Link } from 'react-router-dom';
import clsx from 'clsx';

function TrendingCard({ player }: { player: TrendingPlayerDto }) {
  const warDelta = player.warDelta7d;
  return (
    <Link
      to={`/players/${player.playerId}`}
      className="block p-4 bg-gray-800 rounded-lg hover:bg-gray-700 transition-colors"
    >
      <div className="flex items-center justify-between mb-2">
        <span className="font-semibold text-white text-sm">{player.name}</span>
        <span className="text-xs text-gray-400">{player.position}</span>
      </div>
      <div className="flex items-center justify-between">
        <span className="text-xs text-gray-500">{player.teamId}</span>
        {player.currentWar != null && (
          <span
            className={clsx('text-xs font-medium', {
              'text-green-400': warDelta > 0,
              'text-red-400': warDelta < 0,
              'text-gray-400': warDelta === 0,
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

  if (isLoading) return <div className="text-gray-400 text-sm">Loading trending players...</div>;
  if (error) return <div className="text-red-400 text-sm">Failed to load trending players</div>;
  if (!dashboard) return null;

  return (
    <section>
      <h2 className="text-xl font-bold text-white mb-4">Trending Players</h2>
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div>
          <h3 className="text-sm font-semibold text-gray-400 uppercase tracking-wide mb-3">Hitters</h3>
          <div className="space-y-2">
            {dashboard.trendingHitters.slice(0, 5).map((player) => (
              <TrendingCard key={player.playerId} player={player} />
            ))}
          </div>
        </div>
        <div>
          <h3 className="text-sm font-semibold text-gray-400 uppercase tracking-wide mb-3">Pitchers</h3>
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
