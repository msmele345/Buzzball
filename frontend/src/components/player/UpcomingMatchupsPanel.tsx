// Phase 5: full implementation using MatchupProjection data
export function UpcomingMatchupsPanel({ playerId }: { playerId: string }) {
  return (
    <div className="bg-gray-800 rounded-lg p-6 text-center">
      <p className="text-gray-500 text-sm">Matchup projections coming in Phase 5</p>
      <p className="text-gray-600 text-xs mt-1">Player: {playerId}</p>
    </div>
  );
}
