import type { BattingStatsDto } from '../../types';

interface SplitStatsTableProps {
  currentStats: BattingStatsDto | null;
}

// Placeholder splits table — real split data would come from a dedicated API endpoint
export function SplitStatsTable({ currentStats }: SplitStatsTableProps) {
  if (!currentStats) return <div className="text-text-muted text-sm">No stats available</div>;

  return (
    <div>
      <h3 className="text-xs font-semibold text-text-muted uppercase tracking-[0.15em] mb-3">
        Career Splits (Season Average)
      </h3>
      <table className="w-full text-sm">
        <thead>
          <tr className="text-text-muted text-xs uppercase tracking-wider border-b border-border">
            <th className="text-left py-2">Split</th>
            <th className="text-right py-2 px-2">AVG</th>
            <th className="text-right py-2 px-2">OBP</th>
            <th className="text-right py-2 px-2">SLG</th>
            <th className="text-right py-2">OPS</th>
          </tr>
        </thead>
        <tbody>
          <tr className="border-b border-bg-surface-alt hover:bg-bg-surface-alt transition-colors duration-150">
            <td className="py-2 text-white">Overall</td>
            <td className="text-right py-2 px-2 font-mono text-text-secondary">
              {currentStats.battingAverage?.toFixed(3) ?? '—'}
            </td>
            <td className="text-right py-2 px-2 font-mono text-text-secondary">
              {currentStats.onBasePercentage?.toFixed(3) ?? '—'}
            </td>
            <td className="text-right py-2 px-2 font-mono text-text-secondary">
              {currentStats.sluggingPercentage?.toFixed(3) ?? '—'}
            </td>
            <td className="text-right py-2 font-mono text-text-secondary">
              {currentStats.ops?.toFixed(3) ?? '—'}
            </td>
          </tr>
        </tbody>
      </table>
      <p className="text-xs text-text-muted mt-2 italic">
        Detailed splits (vs L/R, home/away, monthly) available via API integration
      </p>
    </div>
  );
}
