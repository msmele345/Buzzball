import type { PlayerDetailDto } from '../../types';

interface PlayerHeaderProps {
  player: PlayerDetailDto;
}

export function PlayerHeader({ player }: PlayerHeaderProps) {
  return (
    <div className="flex items-center gap-6 p-6 bg-bg-surface border border-border border-t-2 border-t-neon-green rounded-2xl">
      <div className="w-20 h-20 bg-bg-surface-alt border-2 border-neon-green rounded-full flex items-center justify-center text-2xl font-mono font-bold text-neon-green">
        {player.jerseyNumber ? `#${player.jerseyNumber}` : '?'}
      </div>
      <div>
        <h1 className="text-3xl font-extrabold text-white">{player.name}</h1>
        <p className="text-text-secondary mt-1">
          {player.position} · {player.teamId}
        </p>
        {player.batsThrows && (
          <p className="text-sm text-text-muted mt-1">Bats/Throws: {player.batsThrows}</p>
        )}
      </div>
    </div>
  );
}
