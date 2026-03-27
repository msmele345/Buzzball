import {
  BarChart as RechartsBarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer,
  Cell,
} from 'recharts';
import { CHART_GRID, CHART_AXIS, CHART_TOOLTIP, CHART_COLORS } from './chartTheme';

interface BarChartProps {
  data: { name: string; value: number; color?: string }[];
  height?: number;
  color?: string;
  showLegend?: boolean;
}

export function BarChart({ data, height = 300, color = CHART_COLORS.neonGreen, showLegend = false }: BarChartProps) {
  return (
    <ResponsiveContainer width="100%" height={height}>
      <RechartsBarChart data={data} margin={{ top: 5, right: 20, left: 0, bottom: 5 }}>
        <CartesianGrid strokeDasharray={CHART_GRID.strokeDasharray} stroke={CHART_GRID.stroke} />
        <XAxis dataKey="name" {...CHART_AXIS} />
        <YAxis {...CHART_AXIS} />
        <Tooltip {...CHART_TOOLTIP} />
        {showLegend && <Legend />}
        <Bar dataKey="value" fill={color} radius={[6, 6, 0, 0]}>
          {data.map((entry, index) => (
            <Cell key={index} fill={entry.color ?? color} />
          ))}
        </Bar>
      </RechartsBarChart>
    </ResponsiveContainer>
  );
}
