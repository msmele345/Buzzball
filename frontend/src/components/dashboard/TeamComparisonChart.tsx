import { useUiStore } from '../../store/uiStore';
import { useTeamComparison } from '../../hooks/useTeams';
import { RadarChart } from '../charts/RadarChart';
import type { RadarMetric } from '../charts/RadarChart';

const TEAM_COLORS = ['#3b82f6', '#22c55e', '#f59e0b'];

export function TeamComparisonChart() {
  const teamComparisonList = useUiStore((s) => s.teamComparisonList);
  const clearComparison = useUiStore((s) => s.clearComparison);
  const { data: comparison, isLoading } = useTeamComparison(teamComparisonList);

  if (teamComparisonList.length < 2) {
    return (
      <div className="bg-gray-800 rounded-lg p-6 text-center text-gray-400 text-sm">
        Select 2–3 teams from standings to compare
      </div>
    );
  }

  if (isLoading) return <div className="text-gray-400 text-sm">Loading comparison...</div>;
  if (!comparison) return null;

  const metricsForFirstTeam: RadarMetric[] = comparison.metrics.map((metric) => ({
    label: metric.label,
    value: metric.values[0] ?? 0,
    max: Math.max(...metric.values),
  }));

  return (
    <section>
      <div className="flex items-center justify-between mb-4">
        <h2 className="text-xl font-bold text-white">Team Comparison</h2>
        <button
          onClick={clearComparison}
          className="text-xs text-gray-400 hover:text-white transition-colors"
        >
          Clear
        </button>
      </div>
      <div className="flex gap-2 mb-4">
        {comparison.teams.map((team, i) => (
          <span
            key={team.teamId}
            className="px-2 py-1 rounded text-xs font-medium text-white"
            style={{ backgroundColor: TEAM_COLORS[i] }}
          >
            {team.abbreviation}
          </span>
        ))}
      </div>
      <RadarChart metrics={metricsForFirstTeam} />
    </section>
  );
}
