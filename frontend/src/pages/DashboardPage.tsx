import { TrendingPlayersSection } from '../components/dashboard/TrendingPlayersSection';
import { TeamStandingsTable } from '../components/dashboard/TeamStandingsTable';
import { LeagueLeadersCard } from '../components/dashboard/LeagueLeadersCard';
import { TeamComparisonChart } from '../components/dashboard/TeamComparisonChart';

export function DashboardPage() {
  return (
    <div className="max-w-7xl mx-auto px-4 py-10 space-y-12">
      <div>
        <h1 className="text-5xl font-black tracking-tight text-neon-green mb-1">BUZZBALL</h1>
        <p className="text-xs uppercase tracking-[0.2em] text-text-muted">MLB Advanced Metrics &amp; Analytics</p>
      </div>
      <TrendingPlayersSection />
      <LeagueLeadersCard />
      <TeamStandingsTable />
      <TeamComparisonChart />
    </div>
  );
}
