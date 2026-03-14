import { Radar, RadarChart as RechartsRadarChart, PolarGrid, PolarAngleAxis, PolarRadiusAxis, ResponsiveContainer, Tooltip } from 'recharts';

export interface RadarMetric {
  label: string;
  value: number;
  max: number;
}

interface RadarChartProps {
  metrics: RadarMetric[];
  color?: string;
}

export function RadarChart({ metrics, color = '#3b82f6' }: RadarChartProps) {
  const data = metrics.map(({ label, value, max }) => ({
    subject: label,
    value: max > 0 ? (value / max) * 100 : 0,
    fullMark: 100,
  }));

  return (
    <ResponsiveContainer width="100%" height={300}>
      <RechartsRadarChart data={data}>
        <PolarGrid />
        <PolarAngleAxis dataKey="subject" tick={{ fontSize: 12 }} />
        <PolarRadiusAxis angle={90} domain={[0, 100]} tick={false} />
        <Radar name="Player" dataKey="value" stroke={color} fill={color} fillOpacity={0.3} />
        <Tooltip formatter={(value: number) => `${value.toFixed(1)}%`} />
      </RechartsRadarChart>
    </ResponsiveContainer>
  );
}
