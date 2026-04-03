import {useDashboard} from '../../hooks/useDashboard';
import {useUiStore} from '../../store/uiStore';
import type {TeamSummaryDto} from '../../types';
import Loader from "../UI/Loader.tsx";
import { useEffect } from "react";

export interface StandingsTableProps {
    teams: TeamSummaryDto[];
    title: string;
    onCompareSelect: (id: string) => void;
}


function StandingsTable({teams, title, onCompareSelect}: StandingsTableProps) {

    return (
        <div>
            <h3 className="text-xs font-bold text-gold uppercase tracking-[0.15em] mb-2">{title}</h3>
            <div className="overflow-x-auto">
                <table className="w-full text-sm">
                    <thead>
                    <tr className="text-text-muted text-xs uppercase tracking-wider border-b border-border">
                        <th className="text-left py-2 pr-4">Team</th>
                        <th className="text-right py-2 px-2">W</th>
                        <th className="text-right py-2 px-2">L</th>
                        <th className="text-right py-2 px-2">PCT</th>
                        <th className="text-right py-2 px-2">GB</th>
                        <th className="text-right py-2 pl-2">DIFF</th>
                    </tr>
                    </thead>
                    <tbody>
                    {teams.map((team) => (
                        <tr key={team.teamId} onClick={() => onCompareSelect(team.teamId)}
                            className="border-b border-bg-surface-alt hover:bg-bg-surface-alt transition-colors duration-150">
                            <td className="py-2 pr-4 text-white font-semibold">{team.abbreviation}</td>
                            <td className="text-right py-2 px-2 font-mono text-text-secondary">{team.wins}</td>
                            <td className="text-right py-2 px-2 font-mono text-text-secondary">{team.losses}</td>
                            <td className="text-right py-2 px-2 font-mono text-text-secondary">{team.winPct.toFixed(3)}</td>
                            <td className="text-right py-2 px-2 font-mono text-text-muted">
                                {team.gamesBack === 0 ? '—' : team.gamesBack.toFixed(1)}
                            </td>
                            <td
                                className={`text-right py-2 pl-2 font-mono font-medium ${
                                    team.runDifferential > 0 ? 'text-neon-green' : team.runDifferential < 0 ? 'text-hot-red' : 'text-text-muted'
                                }`}
                            >
                                {team.runDifferential > 0 ? '+' : ''}{team.runDifferential}
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            </div>
        </div>
    );
}

const TeamStandingsTable = () => {
    const {data: dashboard, isLoading, error} = useDashboard();
    const {leagueFilter, setTeamComparisonList, teamComparisonList} = useUiStore((s) => s);

    useEffect(() => {
        console.log('teamLIST:', teamComparisonList);
    }, [teamComparisonList]);

    const handleCompareSelect = (id: string) => {
        if (teamComparisonList.includes(id))
            setTeamComparisonList(teamComparisonList.filter(team => team !== id));
        else if (teamComparisonList.length < 2) {
            setTeamComparisonList([...teamComparisonList, id]);
        }
        console.log('Compare selected:', id);
    }

    if (isLoading) return (
        <Loader/>
    );

    if (!dashboard) return null;

    return (
        <>
            {error && <div className="text-hot-red text-sm">Failed to load standings</div>}
            <section>
                <h2 className="text-xl font-bold text-white mb-4 border-l-2 border-neon-green pl-3">Standings</h2>
                <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                    {(leagueFilter === 'all' || leagueFilter === 'AL') && (
                        <StandingsTable teams={dashboard.alStandings} title="American League"
                                        onCompareSelect={handleCompareSelect}/>
                    )}
                    {(leagueFilter === 'all' || leagueFilter === 'NL') && (
                        <StandingsTable teams={dashboard.nlStandings} title="National League"
                                        onCompareSelect={handleCompareSelect}/>
                    )}
                </div>
            </section>
        </>
    );
}

export { TeamStandingsTable };