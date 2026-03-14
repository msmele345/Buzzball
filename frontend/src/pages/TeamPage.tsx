import { useParams } from 'react-router-dom';
import { useTeam, useTeamRoster } from '../hooks/useTeams';
import { Link } from 'react-router-dom';

export function TeamPage() {
  const { id } = useParams<{ id: string }>();
  const { data: team, isLoading, error } = useTeam(id ?? '');
  const { data: roster } = useTeamRoster(id ?? '');

  if (isLoading) return <div className="text-gray-400 p-8">Loading team...</div>;
  if (error) return <div className="text-red-400 p-8">Team not found</div>;
  if (!team) return null;

  return (
    <div className="max-w-5xl mx-auto px-4 py-8 space-y-8">
      <div className="bg-gray-800 rounded-xl p-6">
        <h1 className="text-3xl font-bold text-white">{team.name}</h1>
        <p className="text-gray-400 mt-1">{team.division} · {team.league}</p>
        <div className="flex gap-6 mt-4">
          <div className="text-center">
            <p className="text-2xl font-bold text-white">{team.wins}-{team.losses}</p>
            <p className="text-xs text-gray-400">Record</p>
          </div>
          <div className="text-center">
            <p className="text-2xl font-bold text-white">{team.winPct.toFixed(3)}</p>
            <p className="text-xs text-gray-400">PCT</p>
          </div>
          <div className="text-center">
            <p className={`text-2xl font-bold ${team.runDifferential >= 0 ? 'text-green-400' : 'text-red-400'}`}>
              {team.runDifferential > 0 ? '+' : ''}{team.runDifferential}
            </p>
            <p className="text-xs text-gray-400">Run Diff</p>
          </div>
        </div>
      </div>

      {roster && Array.isArray(roster) && roster.length > 0 && (
        <div>
          <h2 className="text-xl font-bold text-white mb-4">Roster</h2>
          <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-3">
            {roster.map((player: { playerId: string; name: string; position: string; jerseyNumber: string }) => (
              <Link
                key={player.playerId}
                to={`/players/${player.playerId}`}
                className="bg-gray-800 hover:bg-gray-700 rounded-lg p-3 transition-colors"
              >
                <p className="text-white text-sm font-medium">{player.name}</p>
                <p className="text-gray-400 text-xs mt-1">{player.position}</p>
              </Link>
            ))}
          </div>
        </div>
      )}
    </div>
  );
}
