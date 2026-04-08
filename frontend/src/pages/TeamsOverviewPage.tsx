import { useTeams } from '../hooks/useTeams';
import { Link } from 'react-router-dom';

export function TeamsOverviewPage() {
  const { data: teams, isLoading, error } = useTeams();

  if (isLoading) return (
    <div className="max-w-7xl mx-auto px-4 py-8 space-y-4" role="status" aria-label="Loading teams">
      {[...Array(6)].map((_, i) => (
        <div key={i} className="animate-pulse h-16 bg-bg-surface rounded-xl" />
      ))}
    </div>
  );
  if (error) return (
    <div className="max-w-7xl mx-auto px-4 py-16 text-center">
      <p className="text-4xl mb-3" aria-hidden>E-6</p>
      <p className="text-white font-bold text-lg mb-1">Bobbled at short</p>
      <p className="text-text-secondary text-sm">Couldn't load the roster of teams. Give it another try.</p>
    </div>
  );

  const alTeams = teams?.filter((t) => t.league === 'AL') ?? [];
  const nlTeams = teams?.filter((t) => t.league === 'NL') ?? [];

  return (
    <div className="max-w-7xl mx-auto px-4 py-10">
      <h1 className="text-4xl font-extrabold text-white tracking-tight mb-8">Teams</h1>
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
        {([['American League', alTeams], ['National League', nlTeams]] as const).map(([league, leagueTeams]) => (
          <div key={league}>
            <h2 className="text-sm font-bold uppercase tracking-[0.15em] text-gold mb-4">{league}</h2>
            <div className="grid grid-cols-2 sm:grid-cols-3 gap-3">
              {leagueTeams.map((team) => (
                <Link
                  key={team.teamId}
                  to={`/teams/${team.teamId}`}
                  className="bg-bg-surface border border-border rounded-xl p-4 transition-all duration-200 text-center card-press hover:bg-bg-hover hover:border-neon-green/25"
                >
                  <p className="text-white font-bold font-mono text-lg">{team.abbreviation}</p>
                  <p className="text-text-secondary font-mono text-xs mt-1">{team.wins}-{team.losses}</p>
                </Link>
              ))}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
