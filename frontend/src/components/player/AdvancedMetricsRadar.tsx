import { RadarChart } from '../charts/RadarChart';
import type { BattingStatsDto, PitchingStatsDto } from '../../types';

interface AdvancedMetricsRadarProps {
  batting?: BattingStatsDto | null;
  pitching?: PitchingStatsDto | null;
}

export function AdvancedMetricsRadar({ batting, pitching }: AdvancedMetricsRadarProps) {
  if (batting) {
    const metrics = [
      { label: 'xBA', value: batting.xba ?? 0, max: 0.4 },
      { label: 'xwOBA', value: batting.xwoba ?? 0, max: 0.5 },
      { label: 'Barrel%', value: batting.barrelPct ?? 0, max: 25 },
      { label: 'Hard Hit%', value: batting.hardHitPct ?? 0, max: 60 },
      { label: 'Exit Velo', value: batting.exitVelocityAvg ?? 0, max: 100 },
    ];
    return (
      <div>
        <h3 className="text-sm font-semibold text-gray-400 uppercase tracking-wide mb-3">
          Statcast Profile
        </h3>
        <RadarChart metrics={metrics} color="#3b82f6" />
      </div>
    );
  }

  if (pitching) {
    const metrics = [
      { label: 'xERA', value: pitching.xera ? 6 - pitching.xera : 0, max: 6 },
      { label: 'Whiff%', value: pitching.whiffPct ?? 0, max: 40 },
      { label: 'Chase%', value: pitching.chasePct ?? 0, max: 40 },
      { label: 'FIP', value: pitching.fip ? 6 - pitching.fip : 0, max: 6 },
      { label: 'Spin Rate', value: pitching.spinRateFastball ?? 0, max: 2800 },
    ];
    return (
      <div>
        <h3 className="text-sm font-semibold text-gray-400 uppercase tracking-wide mb-3">
          Statcast Profile
        </h3>
        <RadarChart metrics={metrics} color="#f59e0b" />
      </div>
    );
  }

  return null;
}
