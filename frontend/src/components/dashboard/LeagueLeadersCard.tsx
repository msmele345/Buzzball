import { useLeagueLeaders } from '../../hooks/useDashboard';

const CATEGORIES = ['wOBA', 'WAR', 'FIP'];

export function LeagueLeadersCard() {
  const { data: leaders, isLoading, error } = useLeagueLeaders();

  if (isLoading) return <div className="text-gray-400 text-sm">Loading leaders...</div>;
  if (error) return <div className="text-red-400 text-sm">Failed to load league leaders</div>;
  if (!leaders?.length) return null;

  return (
    <section>
      <h2 className="text-xl font-bold text-white mb-4">League Leaders</h2>
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        {CATEGORIES.map((category) => {
          const categoryLeaders = leaders.filter((l) => l.category === category).slice(0, 3);
          return (
            <div key={category} className="bg-gray-800 rounded-lg p-4">
              <h3 className="text-sm font-semibold text-blue-400 uppercase tracking-wide mb-3">{category}</h3>
              <ol className="space-y-2">
                {categoryLeaders.map((leader) => (
                  <li key={leader.playerId} className="flex items-center justify-between">
                    <div>
                      <span className="text-xs text-gray-500 mr-2">{leader.rank}.</span>
                      <span className="text-sm text-white">{leader.playerName}</span>
                    </div>
                    <span className="text-sm font-medium text-gray-200">
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
