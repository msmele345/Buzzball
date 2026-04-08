import {useDashboard} from '../../hooks/useDashboard';
import {useUiStore} from '../../store/uiStore';
import type {TeamSummaryDto} from '../../types';
import Loader from "../UI/Loader.tsx";
import clsx from 'clsx';

export interface StandingsTableProps {
    teams: TeamSummaryDto[];
    title: string;
    selectedTeams: string[];
    onCompareSelect: (id: string) => void;
}


function StandingsTable({teams, title, selectedTeams, onCompareSelect}: StandingsTableProps) {

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
                    {teams.map((team) => {
                        const isSelected = selectedTeams.includes(team.teamId);
                        return (
                        <tr key={team.teamId} onClick={() => onCompareSelect(team.teamId)}
                            className={clsx(
                                "border-b border-bg-surface-alt cursor-pointer transition-colors duration-150",
                                isSelected
                                    ? "bg-neon-green-dim border-l-2 border-l-neon-green"
                                    : "hover:bg-bg-surface-alt"
                            )}>
                            <td className="py-2 pr-4 text-white font-semibold">
                                {isSelected && <span className="inline-block w-1.5 h-1.5 rounded-full bg-neon-green mr-2" />}
                                {team.abbreviation}
                            </td>
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
                    );
                    })}
                    </tbody>
                </table>
            </div>
        </div>
    );
}

const TeamStandingsTable = () => {
    const {data: dashboard, isLoading, error} = useDashboard();
    const {leagueFilter, setTeamComparisonList, teamComparisonList} = useUiStore((s) => s);

    const handleCompareSelect = (id: string) => {
        if (teamComparisonList.includes(id))
            setTeamComparisonList(teamComparisonList.filter(team => team !== id));
        else if (teamComparisonList.length < 2) {
            setTeamComparisonList([...teamComparisonList, id]);
        }
    }

    if (isLoading) return (
        <Loader/>
    );

    if (!dashboard) return null;

    return (
        <>
            {error && <div className="text-hot-red text-sm">The standings got lost in the rain. Failed to load.</div>}
            <section>
                <div className="flex items-center justify-between mb-4">
                    <h2 className="text-xl font-bold text-white border-l-2 border-neon-green pl-3">Standings</h2>
                    <p className="text-xs text-text-muted">Click teams to compare</p>
                </div>
                <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                    {(leagueFilter === 'all' || leagueFilter === 'AL') && (
                        <StandingsTable teams={dashboard.alStandings} title="American League"
                                        selectedTeams={teamComparisonList}
                                        onCompareSelect={handleCompareSelect}/>
                    )}
                    {(leagueFilter === 'all' || leagueFilter === 'NL') && (
                        <StandingsTable teams={dashboard.nlStandings} title="National League"
                                        selectedTeams={teamComparisonList}
                                        onCompareSelect={handleCompareSelect}/>
                    )}
                </div>
            </section>
        </>
    );
}

export { TeamStandingsTable };