package com.buzzball.service;

import com.buzzball.dto.*;
import com.buzzball.mapper.PlayerMapper;
import com.buzzball.mapper.TeamMapper;
import com.buzzball.model.*;
import com.buzzball.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.OptionalInt;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final BattingStatsRepository battingStatsRepository;
    private final PitchingStatsRepository pitchingStatsRepository;
    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;
    private final PlayerMapper playerMapper;
    private final TeamMapper teamMapper;

    @Cacheable("dashboard-trending")
    public DashboardDto getDashboard() {
        return DashboardDto.builder()
                .trendingHitters(getTrendingHitters())
                .trendingPitchers(getTrendingPitchers())
                .alStandings(getStandingsByLeague("AL"))
                .nlStandings(getStandingsByLeague("NL"))
                .leagueLeaders(getLeagueLeaders())
                .build();
    }

    public List<TrendingPlayerDto> getTrendingHitters() {
        List<BattingStats> allStats = new ArrayList<>();
        battingStatsRepository.findAll().forEach(allStats::add);

        OptionalInt latestBattingSeason = allStats.stream()
                .mapToInt(BattingStats::getSeason)
                .max();
        if (latestBattingSeason.isEmpty()) {
            return List.of();
        }
        int currentBattingSeason = latestBattingSeason.getAsInt();

        return allStats.stream()
                .filter(s -> s.getSeason() == currentBattingSeason && s.getFWar() != null)
                .sorted(Comparator.comparingDouble(BattingStats::getFWar).reversed())
                .limit(5)
                .map(stats -> {
                    Player player = playerRepository.findById(stats.getPlayerId()).orElse(null);
                    return TrendingPlayerDto.builder()
                            .playerId(stats.getPlayerId())
                            .name(player != null ? player.getName() : stats.getPlayerId())
                            .teamId(player != null ? player.getTeamId() : "")
                            .position(player != null ? player.getPosition() : "")
                            .currentWar(stats.getFWar())
                            .warDelta7d(0.0)
                            .currentWoba(stats.getWoba())
                            .wobaDelta7d(0.0)
                            .currentFip(null)
                            .fipDelta7d(0.0)
                            .trendCategory("hitting")
                            .build();
                })
                .toList();
    }

    public List<TrendingPlayerDto> getTrendingPitchers() {
        List<PitchingStats> allStats = new ArrayList<>();
        pitchingStatsRepository.findAll().forEach(allStats::add);

        OptionalInt latestPitchingSeason = allStats.stream()
                .mapToInt(PitchingStats::getSeason)
                .max();
        if (latestPitchingSeason.isEmpty()) {
            return List.of();
        }
        int currentPitchingSeason = latestPitchingSeason.getAsInt();

        return allStats.stream()
                .filter(s -> s.getSeason() == currentPitchingSeason && s.getFWar() != null)
                .sorted(Comparator.comparingDouble(PitchingStats::getFWar).reversed())
                .limit(5)
                .map(stats -> {
                    Player player = playerRepository.findById(stats.getPlayerId()).orElse(null);
                    return TrendingPlayerDto.builder()
                            .playerId(stats.getPlayerId())
                            .name(player != null ? player.getName() : stats.getPlayerId())
                            .teamId(player != null ? player.getTeamId() : "")
                            .position(player != null ? player.getPosition() : "")
                            .currentWar(stats.getFWar())
                            .warDelta7d(0.0)
                            .currentWoba(null)
                            .wobaDelta7d(0.0)
                            .currentFip(stats.getFip())
                            .fipDelta7d(0.0)
                            .trendCategory("pitching")
                            .build();
                })
                .toList();
    }

    public List<TeamSummaryDto> getStandingsByLeague(String league) {
        return teamRepository.findByLeague(league)
                .stream()
                .sorted(Comparator.comparingDouble(Team::getWinPct).reversed())
                .map(teamMapper::toSummaryDto)
                .toList();
    }

    public List<LeagueLeaderDto> getLeagueLeaders() {
        List<LeagueLeaderDto> leaders = new ArrayList<>();

        // Top wOBA
        List<BattingStats> battingStats = new ArrayList<>();
        battingStatsRepository.findAll().forEach(battingStats::add);

        OptionalInt latestBattingSeason = battingStats.stream()
                .mapToInt(BattingStats::getSeason)
                .max();
        int currentBattingSeason = latestBattingSeason.orElse(0);

        battingStats.stream()
                .filter(s -> s.getSeason() == currentBattingSeason && s.getWoba() != null)
                .sorted(Comparator.comparingDouble(BattingStats::getWoba).reversed())
                .limit(3)
                .forEach(stats -> {
                    playerRepository.findById(stats.getPlayerId())
                            .ifPresent(p -> {
                                if (p.getTeamId() != null) {
                                    LeagueLeaderDto leagueLeaderDto = LeagueLeaderDto.builder()
                                            .category("wOBA")
                                            .playerId(stats.getPlayerId())
                                            .playerName(p.getName())
                                            .teamId(p.getTeamId())
                                            .value(stats.getWoba())
                                            .rank(leaders.size() + 1)
                                            .build();
                                    leaders.add(leagueLeaderDto);
                                }
                            });
                });

        // Top WAR (batting)
        battingStats.stream()
                .filter(s -> s.getSeason() == currentBattingSeason && s.getFWar() != null)
                .sorted(Comparator.comparingDouble(BattingStats::getFWar).reversed())
                .limit(3)
                .forEach(stats -> {
                    Player player = playerRepository.findById(stats.getPlayerId()).orElse(null);
                    leaders.add(LeagueLeaderDto.builder()
                            .category("WAR")
                            .playerId(stats.getPlayerId())
                            .playerName(player != null ? player.getName() : stats.getPlayerId())
                            .teamId(player != null ? player.getTeamId() : "")
                            .value(stats.getFWar())
                            .rank(leaders.stream().filter(l -> "WAR".equals(l.getCategory())).toList().size() + 1)
                            .build());
                });

        // Top FIP (pitching)
        List<PitchingStats> pitchingStats = new ArrayList<>();
        pitchingStatsRepository.findAll().forEach(pitchingStats::add);

        int currentPitchingSeason = pitchingStats.stream()
                .mapToInt(PitchingStats::getSeason)
                .max()
                .orElse(0);

        pitchingStats.stream()
                .filter(s -> s.getSeason() == currentPitchingSeason && s.getFip() != null)
                .sorted(Comparator.comparingDouble(PitchingStats::getFip))
                .limit(3)
                .forEach(stats -> {
                    Player player = playerRepository.findById(stats.getPlayerId()).orElse(null);
                    leaders.add(LeagueLeaderDto.builder()
                            .category("FIP")
                            .playerId(stats.getPlayerId())
                            .playerName(player != null ? player.getName() : stats.getPlayerId())
                            .teamId(player != null ? player.getTeamId() : "")
                            .value(stats.getFip())
                            .rank(leaders.stream().filter(l -> "FIP".equals(l.getCategory())).toList().size() + 1)
                            .build());
                });

        return leaders;
    }
}
