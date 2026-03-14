import { useTeams } from '../hooks/useTeams';
import { Link } from 'react-router-dom';

export function TeamsOverviewPage() {
  const { data: teams, isLoading, error } = useTeams();

  if (isLoading) return <div className="text-gray-400 p-8">Loading teams...</div>;
  if (error) return <div className="text-red-400 p-8">Failed to load teams</div>;

  const alTeams = teams?.filter((t) => t.league === 'AL') ?? [];
  const nlTeams = teams?.filter((t) => t.league === 'NL') ?? [];

  return (
    <div className="max-w-7xl mx-auto px-4 py-8">
      <h1 className="text-3xl font-bold text-white mb-8">Teams</h1>
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
        {([['American League', alTeams], ['National League', nlTeams]] as const).map(([league, leagueTeams]) => (
          <div key={league}>
            <h2 className="text-lg font-semibold text-gray-300 mb-4">{league}</h2>
            <div className="grid grid-cols-2 sm:grid-cols-3 gap-3">
              {leagueTeams.map((team) => (
                <Link
                  key={team.teamId}
                  to={`/teams/${team.teamId}`}
                  className="bg-gray-800 hover:bg-gray-700 rounded-lg p-4 transition-colors text-center"
                >
                  <p className="text-white font-bold text-lg">{team.abbreviation}</p>
                  <p className="text-gray-400 text-xs mt-1">{team.wins}-{team.losses}</p>
                </Link>
              ))}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
