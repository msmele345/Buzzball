import type { BattingStatsDto, PitchingStatsDto } from '../../types';

interface StatCardProps {
  label: string;
  value: string | number | null;
  unit?: string;
}

function StatCard({ label, value, unit }: StatCardProps) {
  return (
    <div className="bg-gray-800 rounded-lg p-4 text-center">
      <p className="text-xs text-gray-400 uppercase tracking-wide mb-1">{label}</p>
      <p className="text-2xl font-bold text-white">
        {value != null ? (typeof value === 'number' ? value.toFixed(3) : value) : '—'}
        {unit && <span className="text-sm text-gray-400 ml-1">{unit}</span>}
      </p>
    </div>
  );
}

interface StatsSummaryCardsProps {
  batting?: BattingStatsDto | null;
  pitching?: PitchingStatsDto | null;
}

export function StatsSummaryCards({ batting, pitching }: StatsSummaryCardsProps) {
  if (batting) {
    return (
      <div className="grid grid-cols-3 gap-3">
        <StatCard label="WAR" value={batting.fWar} />
        <StatCard label="wOBA" value={batting.woba} />
        <StatCard label="OPS" value={batting.ops} />
      </div>
    );
  }
  if (pitching) {
    return (
      <div className="grid grid-cols-3 gap-3">
        <StatCard label="WAR" value={pitching.fWar} />
        <StatCard label="FIP" value={pitching.fip} />
        <StatCard label="ERA" value={pitching.era} />
      </div>
    );
  }
  return null;
}
