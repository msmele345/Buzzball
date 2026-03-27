import { useParams } from 'react-router-dom';
import { useTeam, useTeamRoster } from '../hooks/useTeams';
import { Link } from 'react-router-dom';

export function TeamPage() {
  const { id } = useParams<{ id: string }>();
  const { data: team, isLoading, error } = useTeam(id ?? '');
  const { data: roster } = useTeamRoster(id ?? '');

  if (isLoading) return (
    <div className="max-w-5xl mx-auto px-4 py-8 space-y-4">
      <div className="animate-pulse h-32 bg-bg-surface rounded-2xl" />
      <div className="animate-pulse h-64 bg-bg-surface rounded-xl" />
    </div>
  );
  if (error) return <div className="text-hot-red p-8">Team not found</div>;
  if (!team) return null;

  return (
    <div className="max-w-5xl mx-auto px-4 py-8 space-y-8">
      <div className="bg-bg-surface border border-border border-t-2 border-t-gold rounded-2xl p-6">
        <h1 className="text-3xl font-extrabold text-white">{team.name}</h1>
        <p className="text-text-secondary mt-1">{team.division} · {team.league}</p>
        <div className="flex gap-6 mt-4">
          <div className="text-center">
            <p className="text-2xl font-bold font-mono text-white">{team.wins}-{team.losses}</p>
            <p className="text-xs uppercase tracking-[0.15em] text-text-muted">Record</p>
          </div>
          <div className="text-center">
            <p className="text-2xl font-bold font-mono text-white">{team.winPct.toFixed(3)}</p>
            <p className="text-xs uppercase tracking-[0.15em] text-text-muted">PCT</p>
          </div>
          <div className="text-center">
            <p className={`text-2xl font-bold font-mono ${team.runDifferential >= 0 ? 'text-neon-green' : 'text-hot-red'}`}>
              {team.runDifferential > 0 ? '+' : ''}{team.runDifferential}
            </p>
            <p className="text-xs uppercase tracking-[0.15em] text-text-muted">Run Diff</p>
          </div>
        </div>
      </div>

      {roster && Array.isArray(roster) && roster.length > 0 && (
        <div>
          <h2 className="text-xl font-bold text-white mb-4 border-l-2 border-neon-green pl-3">Roster</h2>
          <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-3">
            {roster.map((player: { playerId: string; name: string; position: string; jerseyNumber: string }) => (
              <Link
                key={player.playerId}
                to={`/players/${player.playerId}`}
                className="bg-bg-surface border border-border rounded-xl p-3 transition-all duration-200 hover:bg-bg-hover hover:border-neon-green/25 hover:scale-[1.02] hover:shadow-[0_0_15px_rgba(0,255,135,0.1)]"
              >
                <p className="text-white text-sm font-medium">{player.name}</p>
                <p className="text-text-secondary text-xs mt-1">{player.position}</p>
              </Link>
            ))}
          </div>
        </div>
      )}
    </div>
  );
}
