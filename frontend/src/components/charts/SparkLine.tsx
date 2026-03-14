import { LineChart, Line, ResponsiveContainer, Tooltip } from 'recharts';

interface SparkLineProps {
  data: number[];
  color?: string;
  positive?: boolean;
}

export function SparkLine({ data, color, positive }: SparkLineProps) {
  const resolvedColor = color ?? (positive ? '#22c55e' : '#ef4444');
  const chartData = data.map((value, index) => ({ index, value }));

  return (
    <ResponsiveContainer width={80} height={32}>
      <LineChart data={chartData}>
        <Line type="monotone" dataKey="value" stroke={resolvedColor} strokeWidth={1.5} dot={false} />
        <Tooltip
          content={() => null}
          cursor={false}
        />
      </LineChart>
    </ResponsiveContainer>
  );
}
