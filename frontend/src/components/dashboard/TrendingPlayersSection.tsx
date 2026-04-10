import {useDashboard} from '../../hooks/useDashboard';
import type {TrendingPlayerDto} from '../../types';
import {Link} from 'react-router-dom';
import clsx from 'clsx';
import Loader from "../UI/Loader.tsx";
import TrendingPLayersHeader from "../UI/TrendingPlayersHeader.tsx";

interface TrendingCardProps {
    player: TrendingPlayerDto;
    rank: number;
    teamName?: string;
}

function TrendingCard({player, rank}: TrendingCardProps) {
    //see what this will do
    const warDelta = player.warDelta7d;
    const isHot = warDelta >= 0.3;

    return (
        <Link
            to={`/players/${player.playerId}`}
            className={clsx(
                "block p-4 bg-bg-surface border border-border rounded-xl card-press transition-all duration-200 hover:bg-bg-hover hover:border-neon-green/25",
                isHot && "hot-streak"
            )}
        >
            <div className="flex items-center justify-between mb-2">
        <span className="font-semibold text-white text-sm">
          <span className="text-text-muted font-mono text-xs mr-2">{rank}.</span>
            {player.name}
        </span>
                <span className="text-xs text-text-secondary">{player.position}</span>
            </div>
            <div className="flex items-center justify-between">
                <span className="text-xs text-text-muted">{player.teamId}</span>
                {player.currentWar != null && (
                    <span
                        className={clsx('text-xs font-mono font-medium', {
                            'text-neon-green': warDelta > 0,
                            'text-hot-red': warDelta < 0,
                            'text-text-secondary': warDelta === 0,
                        })}
                    >
            WAR: {player.currentWar.toFixed(1)}
                        {warDelta !== 0 && ` (${warDelta > 0 ? '+' : ''}${warDelta.toFixed(2)})`}
          </span>
                )}
            </div>
        </Link>
    );
}

export function TrendingPlayersSection() {
    const {data: dashboard, isLoading, error} = useDashboard();

    if (isLoading) return (
        <Loader
            outerClassName={"space-y-3"}
            itemClassNames={"animate-pulse h-16 bg-bg-surface rounded-xl"}
        />
    );

    if (error) return <div className="text-hot-red text-sm">The scorecard got rained out. Failed to load trending players.</div>;

    if (!dashboard) return null;

    const hasHotStreak =
        [...dashboard.trendingHitters, ...dashboard.trendingPitchers].some(p => p.warDelta7d >= 0.3);

    return (
        <section>
            <TrendingPLayersHeader />
            {hasHotStreak && (
                <p className="text-xs text-text-muted mb-3 italic">Players with a pulsing border are on a hot streak
                    (+0.3 WAR in 7 days)</p>
            )}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div>
                    <h3 className="text-xs font-semibold text-text-muted uppercase tracking-[0.15em] mb-3">Hitters</h3>
                    <div className="space-y-2 card-stagger">
                        {dashboard.trendingHitters.slice(0, 5).map((player, i) => (
                            <TrendingCard key={player.playerId} player={player} rank={i + 1}/>
                        ))}
                    </div>
                </div>
                <div>
                    <h3 className="text-xs font-semibold text-text-muted uppercase tracking-[0.15em] mb-3">Pitchers</h3>
                    <div className="space-y-2 card-stagger">
                        {dashboard.trendingPitchers.slice(0, 5).map((player, i) => (
                            <TrendingCard key={player.playerId} player={player} rank={i + 1}/>
                        ))}
                    </div>
                </div>
            </div>
        </section>
    );
}
