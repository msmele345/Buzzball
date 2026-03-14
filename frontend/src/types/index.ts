// Mirror of backend DTOs

export interface PlayerSummaryDto {
  playerId: string;
  name: string;
  position: string;
  positionAbbrev: string;
  teamId: string;
  active: boolean;
  jerseyNumber: string;
}

export interface PlayerDetailDto extends PlayerSummaryDto {
  birthDate: string;
  batsThrows: string;
}

export interface BattingStatsDto {
  playerId: string;
  season: number;
  gamesPlayed: number;
  atBats: number;
  hits: number;
  homeRuns: number;
  rbi: number;
  walks: number;
  strikeouts: number;
  stolenBases: number;
  battingAverage: number | null;
  onBasePercentage: number | null;
  sluggingPercentage: number | null;
  ops: number | null;
  // Statcast
  xba: number | null;
  xslg: number | null;
  xwoba: number | null;
  exitVelocityAvg: number | null;
  barrelPct: number | null;
  hardHitPct: number | null;
  launchAngleAvg: number | null;
  // FanGraphs
  woba: number | null;
  wrcPlus: number | null;
  fWar: number | null;
  babip: number | null;
}

export interface PitchingStatsDto {
  playerId: string;
  season: number;
  gamesPlayed: number;
  gamesStarted: number;
  inningsPitched: number;
  era: number;
  wins: number;
  losses: number;
  saves: number;
  strikeouts: number;
  walks: number;
  whip: number;
  spinRateFastball: number | null;
  xera: number | null;
  whiffPct: number | null;
  chasePct: number | null;
  fip: number | null;
  xfip: number | null;
  fWar: number | null;
  babip: number | null;
}

export interface FieldingStatsDto {
  playerId: string;
  season: number;
  position: string;
  games: number;
  putouts: number;
  assists: number;
  errors: number;
  fieldingPct: number;
  oaa: number | null;
  drs: number | null;
}

export interface TrendingPlayerDto {
  playerId: string;
  name: string;
  position: string;
  teamId: string;
  currentWar: number | null;
  warDelta7d: number;
  currentWoba: number | null;
  wobaDelta7d: number;
  currentFip: number | null;
  fipDelta7d: number;
  trendCategory: 'hitting' | 'pitching' | 'fielding';
}

export interface TeamSummaryDto {
  teamId: string;
  name: string;
  abbreviation: string;
  division: string;
  league: string;
  wins: number;
  losses: number;
  winPct: number;
  gamesBack: number;
  runDifferential: number;
}

export interface ComparisonMetric {
  metricName: string;
  label: string;
  values: number[];
}

export interface TeamComparisonDto {
  teams: TeamSummaryDto[];
  metrics: ComparisonMetric[];
}

export interface LeagueLeaderDto {
  category: string;
  playerId: string;
  playerName: string;
  teamId: string;
  value: number;
  rank: number;
}

export interface DashboardDto {
  trendingHitters: TrendingPlayerDto[];
  trendingPitchers: TrendingPlayerDto[];
  alStandings: TeamSummaryDto[];
  nlStandings: TeamSummaryDto[];
  leagueLeaders: LeagueLeaderDto[];
}
