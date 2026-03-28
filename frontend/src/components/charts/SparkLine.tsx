import { LineChart, Line, ResponsiveContainer, Tooltip } from 'recharts';
import { CHART_COLORS } from './chartTheme';

interface SparkLineProps {
  data: number[];
  color?: string;
  positive?: boolean;
}

export function SparkLine({ data, color, positive }: SparkLineProps) {
  const resolvedColor = color ?? (positive ? CHART_COLORS.neonGreen : CHART_COLORS.hotRed);
  const chartData = data.map((value, index) => ({ index, value }));

  return (
    <ResponsiveContainer width={80} height={32}>
      <LineChart data={chartData}>
        <Line type="monotone" dataKey="value" stroke={resolvedColor} strokeWidth={2} dot={false} />
        <Tooltip
          content={() => null}
          cursor={false}
        />
      </LineChart>
    </ResponsiveContainer>
  );
}
