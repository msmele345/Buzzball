import { useUiStore } from '../../store/uiStore';
import { useTeamComparison } from '../../hooks/useTeams';
import { RadarChart } from '../charts/RadarChart';
import type { RadarMetric } from '../charts/RadarChart';
import { CHART_COLORS } from '../charts/chartTheme';

const TEAM_COLORS = [CHART_COLORS.neonGreen, CHART_COLORS.gold, CHART_COLORS.electricBlue];

export function TeamComparisonChart() {
  const teamComparisonList = useUiStore((s) => s.teamComparisonList);
  const clearComparison = useUiStore((s) => s.clearComparison);
  const { data: comparison, isLoading } = useTeamComparison(teamComparisonList);

  if (teamComparisonList.length < 2) {
    return (
      <div className="bg-bg-surface border border-border rounded-xl p-6 text-center text-text-secondary text-sm">
        Select 2-3 teams from standings to compare
      </div>
    );
  }

  if (isLoading) return (
    <div className="animate-pulse h-64 bg-bg-surface rounded-xl" />
  );
  if (!comparison) return null;

  const metricsForFirstTeam: RadarMetric[] = comparison.metrics.map((metric) => ({
    label: metric.label,
    value: metric.values[0] ?? 0,
    max: Math.max(...metric.values),
  }));

  return (
    <section>
      <div className="flex items-center justify-between mb-4">
        <h2 className="text-xl font-bold text-white border-l-2 border-neon-green pl-3">Team Comparison</h2>
        <button
          onClick={clearComparison}
          aria-label="Clear team comparison"
          className="text-xs text-text-secondary hover:text-neon-green transition-colors duration-200"
        >
          Clear
        </button>
      </div>
      <div className="flex gap-2 mb-4">
        {comparison.teams.map((team, i) => (
          <span
            key={team.teamId}
            className="px-2 py-1 rounded-lg text-xs font-mono font-medium text-black"
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
