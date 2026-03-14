import type { PlayerDetailDto } from '../../types';

interface PlayerHeaderProps {
  player: PlayerDetailDto;
}

export function PlayerHeader({ player }: PlayerHeaderProps) {
  return (
    <div className="flex items-center gap-6 p-6 bg-gray-800 rounded-xl">
      <div className="w-20 h-20 bg-gray-700 rounded-full flex items-center justify-center text-2xl text-gray-400">
        {player.jerseyNumber ? `#${player.jerseyNumber}` : '?'}
      </div>
      <div>
        <h1 className="text-2xl font-bold text-white">{player.name}</h1>
        <p className="text-gray-400 mt-1">
          {player.position} · {player.teamId}
        </p>
        {player.batsThrows && (
          <p className="text-sm text-gray-500 mt-1">Bats/Throws: {player.batsThrows}</p>
        )}
      </div>
    </div>
  );
}
