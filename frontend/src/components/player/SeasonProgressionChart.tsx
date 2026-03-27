import { LineChart } from '../charts/LineChart';
import { CHART_COLORS } from '../charts/chartTheme';
import type { BattingStatsDto, PitchingStatsDto } from '../../types';

interface SeasonProgressionChartProps {
  battingStats?: BattingStatsDto[];
  pitchingStats?: PitchingStatsDto[];
}

export function SeasonProgressionChart({ battingStats, pitchingStats }: SeasonProgressionChartProps) {
  if (battingStats?.length) {
    const data = battingStats
      .sort((a, b) => a.season - b.season)
      .map((s) => ({ season: String(s.season), woba: s.woba ?? 0, ops: s.ops ?? 0 }));

    return (
      <div>
        <h3 className="text-xs font-semibold text-text-muted uppercase tracking-[0.15em] mb-3">
          Season Progression (wOBA / OPS)
        </h3>
        <LineChart
          data={data}
          xDataKey="season"
          series={[
            { dataKey: 'woba', label: 'wOBA', color: CHART_COLORS.electricBlue },
            { dataKey: 'ops', label: 'OPS', color: CHART_COLORS.neonGreen },
          ]}
        />
      </div>
    );
  }

  if (pitchingStats?.length) {
    const data = pitchingStats
      .sort((a, b) => a.season - b.season)
      .map((s) => ({ season: String(s.season), fip: s.fip ?? 0, era: s.era }));

    return (
      <div>
        <h3 className="text-xs font-semibold text-text-muted uppercase tracking-[0.15em] mb-3">
          Season Progression (FIP / ERA)
        </h3>
        <LineChart
          data={data}
          xDataKey="season"
          series={[
            { dataKey: 'fip', label: 'FIP', color: CHART_COLORS.gold },
            { dataKey: 'era', label: 'ERA', color: CHART_COLORS.hotRed },
          ]}
        />
      </div>
    );
  }

  return <div className="text-text-muted text-sm">No season data available</div>;
}
