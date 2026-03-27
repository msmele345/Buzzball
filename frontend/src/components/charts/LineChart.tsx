import {
  LineChart as RechartsLineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer,
} from 'recharts';
import { CHART_GRID, CHART_AXIS, CHART_TOOLTIP } from './chartTheme';

export interface LineSeries {
  dataKey: string;
  label: string;
  color: string;
}

interface LineChartProps {
  data: Record<string, unknown>[];
  series: LineSeries[];
  xDataKey: string;
  height?: number;
}

export function LineChart({ data, series, xDataKey, height = 300 }: LineChartProps) {
  return (
    <ResponsiveContainer width="100%" height={height}>
      <RechartsLineChart data={data} margin={{ top: 5, right: 20, left: 0, bottom: 5 }}>
        <CartesianGrid strokeDasharray={CHART_GRID.strokeDasharray} stroke={CHART_GRID.stroke} />
        <XAxis dataKey={xDataKey} {...CHART_AXIS} />
        <YAxis {...CHART_AXIS} />
        <Tooltip {...CHART_TOOLTIP} />
        {series.length > 1 && <Legend />}
        {series.map(({ dataKey, label, color }) => (
          <Line
            key={dataKey}
            type="monotone"
            dataKey={dataKey}
            name={label}
            stroke={color}
            strokeWidth={2.5}
            dot={false}
            activeDot={{ r: 6, fill: color, stroke: '#0a0a0a', strokeWidth: 2 }}
          />
        ))}
      </RechartsLineChart>
    </ResponsiveContainer>
  );
}
