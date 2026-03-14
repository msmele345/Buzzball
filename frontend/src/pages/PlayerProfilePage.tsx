import { useParams } from 'react-router-dom';
import { useEffect } from 'react';
import { usePlayer, useBattingStats, usePitchingStats, useFieldingStats } from '../hooks/usePlayers';
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
  const { data: fieldingStats } = useFieldingStats(id ?? '');

  useEffect(() => {
    if (id) addRecentlyViewed(id);
  }, [id, addRecentlyViewed]);

  if (isLoading) return <div className="text-gray-400 p-8">Loading player...</div>;
  if (error) return <div className="text-red-400 p-8">Player not found</div>;
  if (!player) return null;

  const currentBatting = battingStats?.[battingStats.length - 1] ?? null;
  const currentPitching = pitchingStats?.[pitchingStats.length - 1] ?? null;
  const isBatter = currentBatting != null;

  // fieldingStats is fetched but will be used in Phase 5
  void fieldingStats;

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
