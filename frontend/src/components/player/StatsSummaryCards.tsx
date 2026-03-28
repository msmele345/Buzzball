import type { BattingStatsDto, PitchingStatsDto } from '../../types';

interface StatCardProps {
  label: string;
  value: string | number | null;
  unit?: string;
  accentColor?: string;
}

function StatCard({ label, value, unit, accentColor = '#00ff87' }: StatCardProps) {
  return (
    <div className="bg-bg-surface border border-border rounded-xl p-4 text-center" style={{ borderTopWidth: 2, borderTopColor: accentColor }}>
      <p className="text-xs text-text-muted uppercase tracking-[0.15em] mb-1">{label}</p>
      <p className="text-3xl font-bold font-mono text-neon-green">
        {value != null ? (typeof value === 'number' ? value.toFixed(3) : value) : '—'}
        {unit && <span className="text-sm text-text-secondary ml-1">{unit}</span>}
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
        <StatCard label="WAR" value={batting.fWar} accentColor="#00ff87" />
        <StatCard label="wOBA" value={batting.woba} accentColor="#ffd700" />
        <StatCard label="OPS" value={batting.ops} accentColor="#00d4ff" />
      </div>
    );
  }
  if (pitching) {
    return (
      <div className="grid grid-cols-3 gap-3">
        <StatCard label="WAR" value={pitching.fWar} accentColor="#00ff87" />
        <StatCard label="FIP" value={pitching.fip} accentColor="#ffd700" />
        <StatCard label="ERA" value={pitching.era} accentColor="#00d4ff" />
      </div>
    );
  }
  return null;
}
