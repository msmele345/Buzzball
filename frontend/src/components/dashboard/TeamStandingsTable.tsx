import { useDashboard } from '../../hooks/useDashboard';
import { useUiStore } from '../../store/uiStore';
import type { TeamSummaryDto } from '../../types';

function StandingsTable({ teams, title }: { teams: TeamSummaryDto[]; title: string }) {
  return (
    <div>
      <h3 className="text-sm font-semibold text-gray-400 uppercase tracking-wide mb-2">{title}</h3>
      <div className="overflow-x-auto">
        <table className="w-full text-sm">
          <thead>
            <tr className="text-gray-400 border-b border-gray-700">
              <th className="text-left py-2 pr-4">Team</th>
              <th className="text-right py-2 px-2">W</th>
              <th className="text-right py-2 px-2">L</th>
              <th className="text-right py-2 px-2">PCT</th>
              <th className="text-right py-2 px-2">GB</th>
              <th className="text-right py-2 pl-2">DIFF</th>
            </tr>
          </thead>
          <tbody>
            {teams.map((team) => (
              <tr key={team.teamId} className="border-b border-gray-800 hover:bg-gray-800/50">
                <td className="py-2 pr-4 text-white font-medium">{team.abbreviation}</td>
                <td className="text-right py-2 px-2 text-gray-300">{team.wins}</td>
                <td className="text-right py-2 px-2 text-gray-300">{team.losses}</td>
                <td className="text-right py-2 px-2 text-gray-300">{team.winPct.toFixed(3)}</td>
                <td className="text-right py-2 px-2 text-gray-400">
                  {team.gamesBack === 0 ? '—' : team.gamesBack.toFixed(1)}
                </td>
                <td
                  className={`text-right py-2 pl-2 ${
                    team.runDifferential > 0 ? 'text-green-400' : team.runDifferential < 0 ? 'text-red-400' : 'text-gray-400'
                  }`}
                >
                  {team.runDifferential > 0 ? '+' : ''}{team.runDifferential}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}

export function TeamStandingsTable() {
  const { data: dashboard, isLoading, error } = useDashboard();
  const leagueFilter = useUiStore((s) => s.leagueFilter);

  if (isLoading) return <div className="text-gray-400 text-sm">Loading standings...</div>;
  if (error) return <div className="text-red-400 text-sm">Failed to load standings</div>;
  if (!dashboard) return null;

  return (
    <section>
      <h2 className="text-xl font-bold text-white mb-4">Standings</h2>
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {(leagueFilter === 'all' || leagueFilter === 'AL') && (
          <StandingsTable teams={dashboard.alStandings} title="American League" />
        )}
        {(leagueFilter === 'all' || leagueFilter === 'NL') && (
          <StandingsTable teams={dashboard.nlStandings} title="National League" />
        )}
      </div>
    </section>
  );
}
