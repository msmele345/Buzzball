import type { BattingStatsDto, PitchingStatsDto } from '../../types';

interface StatCardProps {
  label: string;
  value: string | number | null;
  unit?: string;
  accentColor?: string;
}

function formatStat(label: string, value: number): string {
  // Match baseball conventions: WAR gets 1 decimal, rate stats get 3
  if (label === 'WAR') return value.toFixed(1);
  return value.toFixed(3);
}

function statTierColor(label: string, value: number): string {
  // Baseball Savant-style tier coloring
  if (label === 'WAR') {
    if (value >= 5) return 'text-neon-green';    // MVP-caliber
    if (value >= 3) return 'text-gold';           // All-Star
    if (value >= 1) return 'text-text-secondary'; // Solid
    return 'text-hot-red';                        // Below replacement
  }
  if (label === 'wOBA') {
    if (value >= 0.370) return 'text-neon-green';
    if (value >= 0.340) return 'text-gold';
    if (value >= 0.310) return 'text-text-secondary';
    return 'text-hot-red';
  }
  if (label === 'OPS') {
    if (value >= 0.900) return 'text-neon-green';
    if (value >= 0.800) return 'text-gold';
    if (value >= 0.700) return 'text-text-secondary';
    return 'text-hot-red';
  }
  if (label === 'FIP' || label === 'ERA') {
    // Lower is better for pitchers
    if (value <= 2.75) return 'text-neon-green';
    if (value <= 3.50) return 'text-gold';
    if (value <= 4.50) return 'text-text-secondary';
    return 'text-hot-red';
  }
  return 'text-neon-green';
}

function StatCard({ label, value, unit, accentColor = '#00ff87' }: StatCardProps) {
  const displayValue = value != null
    ? (typeof value === 'number' ? formatStat(label, value) : value)
    : '—';
  const colorClass = value != null && typeof value === 'number'
    ? statTierColor(label, value)
    : 'text-text-muted';

  return (
    <div className="bg-bg-surface border border-border rounded-xl p-4 text-center" style={{ borderTopWidth: 2, borderTopColor: accentColor }}>
      <p className="text-xs text-text-muted uppercase tracking-[0.15em] mb-1">{label}</p>
      <p className={`text-3xl font-bold font-mono stat-value ${colorClass}`}>
        {displayValue}
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
      <div className="grid grid-cols-3 gap-3 card-stagger">
        <StatCard label="WAR" value={batting.fWar} accentColor="#00ff87" />
        <StatCard label="wOBA" value={batting.woba} accentColor="#ffd700" />
        <StatCard label="OPS" value={batting.ops} accentColor="#00d4ff" />
      </div>
    );
  }
  if (pitching) {
    return (
      <div className="grid grid-cols-3 gap-3 card-stagger">
        <StatCard label="WAR" value={pitching.fWar} accentColor="#00ff87" />
        <StatCard label="FIP" value={pitching.fip} accentColor="#ffd700" />
        <StatCard label="ERA" value={pitching.era} accentColor="#00d4ff" />
      </div>
    );
  }
  return null;
}
