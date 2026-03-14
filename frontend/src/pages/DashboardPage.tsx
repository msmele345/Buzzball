import { TrendingPlayersSection } from '../components/dashboard/TrendingPlayersSection';
import { TeamStandingsTable } from '../components/dashboard/TeamStandingsTable';
import { LeagueLeadersCard } from '../components/dashboard/LeagueLeadersCard';
import { TeamComparisonChart } from '../components/dashboard/TeamComparisonChart';

export function DashboardPage() {
  return (
    <div className="max-w-7xl mx-auto px-4 py-8 space-y-10">
      <div>
        <h1 className="text-3xl font-bold text-white mb-2">BuzzBall Dashboard</h1>
        <p className="text-gray-400">MLB Advanced Metrics & Analytics</p>
      </div>
      <TrendingPlayersSection />
      <LeagueLeadersCard />
      <TeamStandingsTable />
      <TeamComparisonChart />
    </div>
  );
}
