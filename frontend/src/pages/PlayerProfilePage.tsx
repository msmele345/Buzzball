import { useParams } from 'react-router-dom';
import { useEffect } from 'react';
import { usePlayer, useBattingStats, usePitchingStats } from '../hooks/usePlayers';
import { usePlayerStore } from '../store/playerStore';
import { PlayerHeader } from '../components/player/PlayerHeader';
import { StatsSummaryCards } from '../components/player/StatsSummaryCards';
import { SeasonProgressionChart } from '../components/player/SeasonProgressionChart';
import { AdvancedMetricsRadar } from '../components/player/AdvancedMetricsRadar';
import { SplitStatsTable } from '../components/player/SplitStatsTable';
import { UpcomingMatchupsPanel } from '../components/player/UpcomingMatchupsPanel';

export function PlayerProfilePage() {
  const { id } = useParams<{ id: string }>();
  const addRecentlyViewed = usePlayerStore((s) => s.addRecentlyViewed);

  const { data: player, isLoading, error } = usePlayer(id ?? '');
  const { data: battingStats } = useBattingStats(id ?? '');
  const { data: pitchingStats } = usePitchingStats(id ?? '');
  useEffect(() => {
    if (id) addRecentlyViewed(id);
  }, [id, addRecentlyViewed]);

  if (isLoading) return (
    <div className="max-w-5xl mx-auto px-4 py-8 space-y-4" role="status" aria-label="Loading player profile">
      <div className="animate-pulse h-24 bg-bg-surface rounded-2xl" />
      <div className="grid grid-cols-3 gap-3">
        <div className="animate-pulse h-20 bg-bg-surface rounded-xl" />
        <div className="animate-pulse h-20 bg-bg-surface rounded-xl" />
        <div className="animate-pulse h-20 bg-bg-surface rounded-xl" />
      </div>
    </div>
  );
  if (error) return <div className="text-hot-red p-8">Player not found</div>;
  if (!player) return null;

  const currentBatting = battingStats?.[battingStats.length - 1] ?? null;
  const currentPitching = pitchingStats?.[pitchingStats.length - 1] ?? null;
  const isBatter = currentBatting != null;

  return (
    <div className="max-w-5xl mx-auto px-4 py-8 space-y-8">
      <PlayerHeader player={player} />
      <StatsSummaryCards batting={currentBatting} pitching={currentPitching} />
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <SeasonProgressionChart
          battingStats={isBatter ? battingStats : undefined}
          pitchingStats={!isBatter ? pitchingStats : undefined}
        />
        <AdvancedMetricsRadar batting={currentBatting} pitching={currentPitching} />
      </div>
      <SplitStatsTable currentStats={currentBatting} />
      <UpcomingMatchupsPanel playerId={id ?? ''} />
    </div>
  );
}
