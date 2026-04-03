package com.buzzball.service.ingestion;

import com.buzzball.model.Player;
import com.buzzball.model.Team;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class MlbStatsApiClient {

    private final RestClient restClient;

    public MlbStatsApiClient(RestClient.Builder restClientBuilder,
                             @Value("${buzzball.clients.mlb.base-url:https://statsapi.mlb.com/api/v1}") String baseUrl) {
        this.restClient = restClientBuilder
                .baseUrl(baseUrl)
                .build();
    }

    @CircuitBreaker(name = "mlbstatsapi", fallbackMethod = "fetchStandingsFallback")
    @SuppressWarnings("unchecked")
    public List<Team> fetchStandings() {
        log.info("Fetching MLB standings from Stats API");
        try {
            Map<String, Object> response = restClient.get()
                    .uri("/standings?leagueId=103,104&season={season}&standingsTypes=regularSeason&hydrate=team",
                            getCurrentSeason())
                    .retrieve()
                    .body(Map.class);

            return parseStandingsResponse(response);
        } catch (Exception e) {
            log.error("Failed to fetch standings: {}", e.getMessage(), e);
            return List.of();
        }
    }

    @CircuitBreaker(name = "mlbstatsapi", fallbackMethod = "fetchRosterFallback")
    @SuppressWarnings("unchecked")
    public List<Player> fetchRosterForTeam(String teamId) {
        log.info("Fetching roster for team {}", teamId);
        try {
            Map<String, Object> response = restClient.get()
                    .uri("/teams/{teamId}/roster?rosterType=active&season={season}",
                            teamId, getCurrentSeason())
                    .retrieve()
                    .body(Map.class);

            return parseRosterResponse(response, teamId);
        } catch (Exception e) {
            log.error("Failed to fetch roster for team {}: {}", teamId, e.getMessage(), e);
            return List.of();
        }
    }

    @SuppressWarnings("unused")
    private List<Team> fetchStandingsFallback(Throwable t) {
        log.warn("MLB Stats API circuit breaker open for standings: {}", t.getMessage());
        return Collections.emptyList();
    }

    @SuppressWarnings("unused")
    private List<Player> fetchRosterFallback(String teamId, Throwable t) {
        log.warn("MLB Stats API circuit breaker open for team {}: {}", teamId, t.getMessage());
        return Collections.emptyList();
    }

    private int getCurrentSeason() {
        return java.time.Year.now().getValue();
    }

    @SuppressWarnings("unchecked")
    private List<Team> parseStandingsResponse(Map<String, Object> response) {
        List<Team> teams = new ArrayList<>();
        if (response == null) return teams;

        List<Map<String, Object>> records = (List<Map<String, Object>>) response.get("records");
        if (records == null) return teams;

        for (Map<String, Object> record : records) {
            List<Map<String, Object>> teamRecords = (List<Map<String, Object>>) record.get("teamRecords");
            if (teamRecords == null) continue;

            for (Map<String, Object> teamRecord : teamRecords) {
                teams.add(buildTeamFromRecord(teamRecord));
            }
        }
        return teams;
    }

    @SuppressWarnings("unchecked")
    private Team buildTeamFromRecord(Map<String, Object> teamRecord) {
        Map<String, Object> teamInfo = (Map<String, Object>) teamRecord.get("team");
        Map<String, Object> divisionInfo = (Map<String, Object>) teamInfo.get("division");
        Map<String, Object> leagueInfo = (Map<String, Object>) teamInfo.get("league");

        String division = divisionInfo != null ? (String) divisionInfo.get("name") : "Unknown";
        String league = extractLeagueAbbreviation(leagueInfo);

        return Team.builder()
                .teamId(String.valueOf(teamInfo.get("id")))
                .name((String) teamInfo.get("name"))
                .abbreviation((String) teamInfo.getOrDefault("abbreviation", ""))
                .division(division)
                .league(league)
                .wins(parseIntSafe(teamRecord.get("wins")))
                .losses(parseIntSafe(teamRecord.get("losses")))
                .winPct(parseDoubleSafe(teamRecord.get("winningPercentage")))
                .gamesBack(parseDoubleSafe(teamRecord.get("gamesBack")))
                .build();
    }

    private String extractLeagueAbbreviation(Map<String, Object> leagueInfo) {
        if (leagueInfo == null) return "Unknown";
        Object id = leagueInfo.get("id");
        if (id != null) {
            int leagueId = parseIntSafe(id);
            if (leagueId == 103) return "AL";
            if (leagueId == 104) return "NL";
        }
        return "Unknown";
    }

    @SuppressWarnings("unchecked")
    private List<Player> parseRosterResponse(Map<String, Object> response, String teamId) {
        List<Player> players = new ArrayList<>();
        if (response == null) return players;

        List<Map<String, Object>> roster = (List<Map<String, Object>>) response.get("roster");
        if (roster == null) return players;

        for (Map<String, Object> entry : roster) {
            Map<String, Object> personInfo = (Map<String, Object>) entry.get("person");
            Map<String, Object> positionInfo = (Map<String, Object>) entry.get("position");
            if (personInfo == null) continue;

            players.add(Player.builder()
                    .playerId(String.valueOf(personInfo.get("id")))
                    .name((String) personInfo.get("fullName"))
                    .teamId(teamId)
                    .position(positionInfo != null ? (String) positionInfo.get("name") : "")
                    .positionAbbrev(positionInfo != null ? (String) positionInfo.get("abbreviation") : "")
                    .jerseyNumber((String) entry.getOrDefault("jerseyNumber", ""))
                    .active(true)
                    .build());
        }
        return players;
    }

    private int parseIntSafe(Object value) {
        if (value == null) return 0;
        try { return Integer.parseInt(String.valueOf(value)); } catch (Exception e) { return 0; }
    }

    private double parseDoubleSafe(Object value) {
        if (value == null) return 0.0;
        try { return Double.parseDouble(String.valueOf(value)); } catch (Exception e) { return 0.0; }
    }
}
