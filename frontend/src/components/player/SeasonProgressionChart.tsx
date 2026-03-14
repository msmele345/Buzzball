import { LineChart } from '../charts/LineChart';
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
        <h3 className="text-sm font-semibold text-gray-400 uppercase tracking-wide mb-3">
          Season Progression (wOBA / OPS)
        </h3>
        <LineChart
          data={data}
          xDataKey="season"
          series={[
            { dataKey: 'woba', label: 'wOBA', color: '#3b82f6' },
            { dataKey: 'ops', label: 'OPS', color: '#22c55e' },
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
        <h3 className="text-sm font-semibold text-gray-400 uppercase tracking-wide mb-3">
          Season Progression (FIP / ERA)
        </h3>
        <LineChart
          data={data}
          xDataKey="season"
          series={[
            { dataKey: 'fip', label: 'FIP', color: '#f59e0b' },
            { dataKey: 'era', label: 'ERA', color: '#ef4444' },
          ]}
        />
      </div>
    );
  }

  return <div className="text-gray-500 text-sm">No season data available</div>;
}
