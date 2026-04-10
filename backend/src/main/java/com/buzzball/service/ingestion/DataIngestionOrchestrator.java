package com.buzzball.service.ingestion;

import com.buzzball.model.*;
import com.buzzball.repository.BattingStatsRepository;
import com.buzzball.repository.PitchingStatsRepository;
import com.buzzball.repository.PlayerRepository;
import com.buzzball.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataIngestionOrchestrator {

    private final MlbStatsApiClient mlbStatsApiClient;
    private final StatcastClient statcastClient;
    private final FanGraphsClient fanGraphsClient;
    private final PlayerRepository playerRepository;
    private final BattingStatsRepository battingStatsRepository;
    private final PitchingStatsRepository pitchingStatsRepository;
    private final TeamRepository teamRepository;

    // All 30 MLB team IDs
    private static final List<String> MLB_TEAM_IDS = List.of(
            "108", "109", "110", "111", "112", "113", "114", "115", "116", "117",
            "118", "119", "120", "121", "133", "134", "135", "136", "137", "138",
            "139", "140", "141", "142", "143", "144", "145", "146", "147", "158"
    );

    public void refreshRostersAndStandings() {
        log.info("Starting roster and standings refresh");
        for (String teamId : MLB_TEAM_IDS) {
            try {
                List<Player> players = mlbStatsApiClient.fetchRosterForTeam(teamId);
                playerRepository.saveAll(players);
                log.info("Upserted {} players for team {}", players.size(), teamId);
            } catch (Exception e) {
                log.error("Failed to refresh roster for team {}: {}", teamId, e.getMessage(), e);
            }
        }
        log.info("Roster and standings refresh complete");
    }

    public void refreshStatcastData(int season) {
        log.info("Starting Statcast data refresh for season {}", season);

        List<StatcastRow> battingRows = statcastClient.fetchBattingStatcast(season);
        if (battingRows.isEmpty() && season > 2020) {
            log.warn("No Statcast batting data for {} — falling back to {}", season, season - 1);
            season = season - 1;
            battingRows = statcastClient.fetchBattingStatcast(season);
        }

        Map<String, StatcastRow> battingByPlayerId = battingRows.stream()
                .collect(Collectors.toMap(StatcastRow::getPlayerId, r -> r, (a, b) -> a));

        for (Map.Entry<String, StatcastRow> entry : battingByPlayerId.entrySet()) {
            try {
                updateBattingWithStatcast(entry.getKey(), entry.getValue(), season);
            } catch (Exception e) {
                log.error("Failed to update batting Statcast for player {}: {}", entry.getKey(), e.getMessage());
            }
        }

        List<StatcastRow> pitchingRows = statcastClient.fetchPitchingStatcast(season);
        if (pitchingRows.isEmpty() && season > 2020) {
            pitchingRows = statcastClient.fetchPitchingStatcast(season - 1);
        }
        for (StatcastRow row : pitchingRows) {
            try {
                updatePitchingWithStatcast(row.getPlayerId(), row, season);
            } catch (Exception e) {
                log.error("Failed to update pitching Statcast for player {}: {}", row.getPlayerId(), e.getMessage());
            }
        }

        log.info("Statcast data refresh complete — {} batters, {} pitchers", battingByPlayerId.size(), pitchingRows.size());
    }

    public void refreshFanGraphsData(int season) {
        log.info("Starting FanGraphs data refresh for season {}", season);

        List<Map<String, Object>> battingData = fanGraphsClient.fetchBattingLeaderboard(season);
        if (battingData.isEmpty() && season > 2020) {
            log.warn("FanGraphs batting data unavailable for {} — falling back to {}", season, season - 1);
            season = season - 1;
            battingData = fanGraphsClient.fetchBattingLeaderboard(season);
        }
        if (battingData.isEmpty()) {
            log.warn("FanGraphs batting data unavailable (circuit open or empty) — serving stale data");
        } else {
            for (Map<String, Object> row : battingData) {
                try {
                    updateBattingWithFanGraphs(row, season);
                } catch (Exception e) {
                    log.error("Failed to update FanGraphs batting for row: {}", e.getMessage());
                }
            }
        }

        List<Map<String, Object>> pitchingData = fanGraphsClient.fetchPitchingLeaderboard(season);
        if (pitchingData.isEmpty() && season > 2020) {
            pitchingData = fanGraphsClient.fetchPitchingLeaderboard(season - 1);
        }
        if (pitchingData.isEmpty()) {
            log.warn("FanGraphs pitching data unavailable (circuit open or empty) — serving stale data");
        } else {
            for (Map<String, Object> row : pitchingData) {
                try {
                    updatePitchingWithFanGraphs(row, season);
                } catch (Exception e) {
                    log.error("Failed to update FanGraphs pitching for row: {}", e.getMessage());
                }
            }
        }

        log.info("FanGraphs data refresh complete");
    }

    public void refreshStandings() {
        purgeStaleTeamDocuments();
//        purgeStalePlayerDocuments();
        List<Team> teams = mlbStatsApiClient.fetchStandings();
        teams.stream()
                .filter(t -> t.getDivision() != null)
                .forEach(teamRepository::save);
        log.info("Upserted {} team standings", teams.size());
    }

    public void purgeStaleTeamDocuments() {
        log.info("Purging stale team documents with null division");
        List<Team> allTeams = new ArrayList<>();
        teamRepository.findAll().forEach(allTeams::add);

        List<Team> staleTeams = allTeams.stream()
                .filter(t -> t.getDivision() == null)
                .toList();

        for (Team stale : staleTeams) {
            try {
                teamRepository.delete(stale);
                log.info("Deleted stale team document: id={}", stale.getTeamId());
            } catch (Exception e) {
                log.error("Failed to delete stale team {}: {}", stale.getTeamId(), e.getMessage());
            }
        }
        log.info("Purge complete: removed {} stale documents", staleTeams.size());
    }

    public void purgeStalePlayerDocuments() {
        List<Player> allPlayers = new ArrayList<>();
        playerRepository.findAll().forEach(allPlayers::add);

        Pattern numericPattern = Pattern.compile(".*\\d+.*");

        List<Player> stalePlayers = allPlayers.stream()
                .filter(p -> p.getName() == null || numericPattern.matcher(p.getName()).matches())
                .toList();

        for (Player stale : stalePlayers) {
            try {
                playerRepository.delete(stale);
                log.info("stale player document count: {}", stalePlayers.size());
                log.info("Deleted stale player document: id={}", stale.getPlayerId());
            } catch (Exception e) {
                log.error("Failed to delete stale player {}: {}", stale.getPlayerId(), e.getMessage());
            }
        }
    }

    private void updateBattingWithStatcast(String playerId, StatcastRow row, int season) {
        String id = playerId + "-" + season;
        BattingStats stats = battingStatsRepository.findById(id)
                .orElse(BattingStats.builder().id(id).playerId(playerId).season(season).build());

        stats.setXba(parseDoubleSafe(row.getXba()));
        stats.setXslg(parseDoubleSafe(row.getXslg()));
        stats.setXwoba(parseDoubleSafe(row.getXwoba()));
        stats.setExitVelocityAvg(parseDoubleSafe(row.getExitVelocityAvg()));
        stats.setBarrelPct(parseDoubleSafe(row.getBarrelPct()));
        stats.setHardHitPct(parseDoubleSafe(row.getHardHitPct()));
        stats.setLaunchAngleAvg(parseDoubleSafe(row.getLaunchAngleAvg()));
        stats.setSprintSpeedFt(parseDoubleSafe(row.getSprintSpeed()));

        battingStatsRepository.save(stats);
    }

    private void updatePitchingWithStatcast(String playerId, StatcastRow row, int season) {
        String id = playerId + "-" + season;
        PitchingStats stats = pitchingStatsRepository.findById(id)
                .orElse(PitchingStats.builder().id(id).playerId(playerId).season(season).build());

        stats.setSpinRateFastball(parseDoubleSafe(row.getSpinRateAvg()));
        stats.setXera(parseDoubleSafe(row.getXera()));
        stats.setWhiffPct(parseDoubleSafe(row.getWhiffPct()));
        stats.setChasePct(parseDoubleSafe(row.getChasePct()));
        stats.setXba(parseDoubleSafe(row.getXba()));

        pitchingStatsRepository.save(stats);
    }

    private void updateBattingWithFanGraphs(Map<String, Object> row, int season) {
        String playerId = String.valueOf(row.getOrDefault("xMLBAMID", ""));
        if (playerId.isEmpty() || "null".equals(playerId)) return;

        String id = playerId + "-" + season;
        BattingStats stats = battingStatsRepository.findById(id)
                .orElse(BattingStats.builder().id(id).playerId(playerId).season(season).build());

        stats.setOps(parseDoubleSafe(String.valueOf(row.getOrDefault("OPS", ""))));
        stats.setWoba(parseDoubleSafe(String.valueOf(row.getOrDefault("wOBA", ""))));
        stats.setWrcPlus(parseDoubleSafe(String.valueOf(row.getOrDefault("wRC+", ""))));
        stats.setFWar(parseDoubleSafe(String.valueOf(row.getOrDefault("WAR", ""))));
        stats.setBabip(parseDoubleSafe(String.valueOf(row.getOrDefault("BABIP", ""))));

        battingStatsRepository.save(stats);
    }

    private void updatePitchingWithFanGraphs(Map<String, Object> row, int season) {
        String playerId = String.valueOf(row.getOrDefault("xMLBAMID", ""));
        if (playerId.isEmpty() || "null".equals(playerId)) return;

        String id = playerId + "-" + season;
        PitchingStats stats = pitchingStatsRepository.findById(id)
                .orElse(PitchingStats.builder().id(id).playerId(playerId).season(season).build());

        stats.setFip(parseDoubleSafe(String.valueOf(row.getOrDefault("FIP", ""))));
        stats.setXfip(parseDoubleSafe(String.valueOf(row.getOrDefault("xFIP", ""))));
        stats.setFWar(parseDoubleSafe(String.valueOf(row.getOrDefault("WAR", ""))));
        stats.setBabip(parseDoubleSafe(String.valueOf(row.getOrDefault("BABIP", ""))));

        pitchingStatsRepository.save(stats);
    }

    private Double parseDoubleSafe(String value) {
        if (value == null || value.isBlank() || "null".equalsIgnoreCase(value) || "N/A".equalsIgnoreCase(value)) {
            return null;
        }
        try {
            return Double.parseDouble(value.trim().replace("%", ""));
        } catch (Exception e) {
            return null;
        }
    }
}
