import { TrendingPlayersSection } from '../components/dashboard/TrendingPlayersSection';
import { TeamStandingsTable } from '../components/dashboard/TeamStandingsTable';
import { LeagueLeadersCard } from '../components/dashboard/LeagueLeadersCard';
import { TeamComparisonChart } from '../components/dashboard/TeamComparisonChart';

export function DashboardPage() {
  return (
    <div className="max-w-7xl mx-auto px-4 py-10 space-y-12">
      <div>
        <h1 className="text-3xl font-extrabold tracking-tight text-white">Dashboard</h1>
        <p className="text-xs uppercase tracking-[0.2em] text-text-muted mt-1">MLB Advanced Metrics &amp; Analytics</p>
      </div>
      <TrendingPlayersSection />
      <LeagueLeadersCard />
      <TeamStandingsTable />
      <TeamComparisonChart />
    </div>
  );
}
