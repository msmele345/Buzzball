import { useLeagueLeaders } from '../../hooks/useDashboard';
import Loader from "../UI/Loader.tsx";

const CATEGORIES = ['wOBA', 'WAR', 'FIP'];

export function LeagueLeadersCard() {
  const { data: leaders, isLoading, error } = useLeagueLeaders();

  if (isLoading) return (
      <Loader
          outerClassName={"grid grid-cols-1 sm:grid-cols-3 gap-4"}
          itemClassNames={"animate-pulse h-32 bg-bg-surface rounded-xl"}
      />
  );
  if (error) return <div className="text-hot-red text-sm">Rain delay on the leaderboard. Failed to load league leaders.</div>;

  if (!leaders?.length) return null;

  return (
    <section>
      <h2 className="text-xl font-bold text-white mb-4 border-l-2 border-neon-green pl-3">League Leaders</h2>
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 card-stagger">
        {CATEGORIES.map((category) => {
          const categoryLeaders = leaders.filter((l) => l.category === category).slice(0, 3);
          return (
            <div key={category} className="bg-bg-surface border border-border rounded-xl p-4">
              <h3 className="text-xs font-semibold text-electric-blue uppercase tracking-[0.15em] mb-3">{category}</h3>
              <ol className="space-y-2">
                {categoryLeaders.map((leader) => (
                  <li key={leader.playerId} className="flex items-center justify-between">
                    <div>
                      <span className="text-xs text-text-muted mr-2">{leader.rank}.</span>
                      <span className="text-sm text-white">{leader.playerName}</span>
                    </div>
                    <span className="text-sm font-mono font-medium text-neon-green">
                      {leader.value.toFixed(3)}
                    </span>
                  </li>
                ))}
              </ol>
            </div>
          );
        })}
      </div>
    </section>
  );
}
