package com.buzzball.service.ingestion;

import com.buzzball.model.*;
import com.buzzball.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
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
        refreshStandings();
        for (String teamId : MLB_TEAM_IDS) {
            try {
                List<Player> players = mlbStatsApiClient.fetchRosterForTeam(teamId);
                players.forEach(player -> playerRepository.save(player));
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

    private void refreshStandings() {
        List<Team> teams = mlbStatsApiClient.fetchStandings();
        teams.forEach(teamRepository::save);
        log.info("Upserted {} team standings", teams.size());
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
        String playerId = String.valueOf(row.getOrDefault("playerid", ""));
        if (playerId.isEmpty() || "null".equals(playerId)) return;

        String id = playerId + "-" + season;
        BattingStats stats = battingStatsRepository.findById(id)
                .orElse(BattingStats.builder().id(id).playerId(playerId).season(season).build());

        stats.setWoba(parseDoubleSafe(String.valueOf(row.getOrDefault("wOBA", ""))));
        stats.setWrcPlus(parseDoubleSafe(String.valueOf(row.getOrDefault("wRC+", ""))));
        stats.setFWar(parseDoubleSafe(String.valueOf(row.getOrDefault("WAR", ""))));
        stats.setBabip(parseDoubleSafe(String.valueOf(row.getOrDefault("BABIP", ""))));

        battingStatsRepository.save(stats);
    }

    private void updatePitchingWithFanGraphs(Map<String, Object> row, int season) {
        String playerId = String.valueOf(row.getOrDefault("playerid", ""));
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
        try { return Double.parseDouble(value.replace("%", "")); } catch (Exception e) { return null; }
    }
}
