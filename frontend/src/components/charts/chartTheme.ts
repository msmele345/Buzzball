export const CHART_COLORS = {
  neonGreen: '#00ff87',
  gold: '#ffd700',
  electricBlue: '#00d4ff',
  hotRed: '#ff3366',
  purple: '#a855f7',
} as const;

export const CHART_GRID = {
  stroke: '#1a1a1a',
  strokeDasharray: '3 3',
} as const;

export const CHART_AXIS = {
  tick: { fill: '#666666', fontSize: 11 },
} as const;

export const CHART_TOOLTIP = {
  contentStyle: {
    backgroundColor: '#141414',
    border: '1px solid #2a2a2a',
    borderRadius: 8,
    color: '#ffffff',
    fontSize: 12,
  },
} as const;
