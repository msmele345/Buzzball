import type { BattingStatsDto } from '../../types';

interface SplitStatsTableProps {
  currentStats: BattingStatsDto | null;
}

// Placeholder splits table — real split data would come from a dedicated API endpoint
export function SplitStatsTable({ currentStats }: SplitStatsTableProps) {
  if (!currentStats) return <div className="text-gray-500 text-sm">No stats available</div>;

  return (
    <div>
      <h3 className="text-sm font-semibold text-gray-400 uppercase tracking-wide mb-3">
        Career Splits (Season Average)
      </h3>
      <table className="w-full text-sm">
        <thead>
          <tr className="text-gray-400 border-b border-gray-700">
            <th className="text-left py-2">Split</th>
            <th className="text-right py-2 px-2">AVG</th>
            <th className="text-right py-2 px-2">OBP</th>
            <th className="text-right py-2 px-2">SLG</th>
            <th className="text-right py-2">OPS</th>
          </tr>
        </thead>
        <tbody>
          <tr className="border-b border-gray-800">
            <td className="py-2 text-white">Overall</td>
            <td className="text-right py-2 px-2 text-gray-300">
              {currentStats.battingAverage?.toFixed(3) ?? '—'}
            </td>
            <td className="text-right py-2 px-2 text-gray-300">
              {currentStats.onBasePercentage?.toFixed(3) ?? '—'}
            </td>
            <td className="text-right py-2 px-2 text-gray-300">
              {currentStats.sluggingPercentage?.toFixed(3) ?? '—'}
            </td>
            <td className="text-right py-2 text-gray-300">
              {currentStats.ops?.toFixed(3) ?? '—'}
            </td>
          </tr>
        </tbody>
      </table>
      <p className="text-xs text-gray-600 mt-2">
        Detailed splits (vs L/R, home/away, monthly) available via API integration
      </p>
    </div>
  );
}
