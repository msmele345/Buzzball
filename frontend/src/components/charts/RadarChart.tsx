import { Radar, RadarChart as RechartsRadarChart, PolarGrid, PolarAngleAxis, PolarRadiusAxis, ResponsiveContainer, Tooltip } from 'recharts';
import { CHART_COLORS, CHART_TOOLTIP } from './chartTheme';

export interface RadarMetric {
  label: string;
  value: number;
  max: number;
}

interface RadarChartProps {
  metrics: RadarMetric[];
  color?: string;
}

export function RadarChart({ metrics, color = CHART_COLORS.neonGreen }: RadarChartProps) {
  const data = metrics.map(({ label, value, max }) => ({
    subject: label,
    value: max > 0 ? (value / max) * 100 : 0,
    fullMark: 100,
  }));

  return (
    <ResponsiveContainer width="100%" height={300}>
      <RechartsRadarChart data={data}>
        <PolarGrid stroke="#1f1f1f" />
        <PolarAngleAxis dataKey="subject" tick={{ fill: '#a0a0a0', fontSize: 11 }} />
        <PolarRadiusAxis angle={90} domain={[0, 100]} tick={false} />
        <Radar name="Player" dataKey="value" stroke={color} fill={color} fillOpacity={0.15} strokeWidth={2} />
        <Tooltip {...CHART_TOOLTIP} formatter={(value) => `${Number(value).toFixed(1)}%`} />
      </RechartsRadarChart>
    </ResponsiveContainer>
  );
}
