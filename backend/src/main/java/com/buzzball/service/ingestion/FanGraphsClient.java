package com.buzzball.service.ingestion;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class FanGraphsClient {

    private static final String FANGRAPHS_BASE_URL = "https://www.fangraphs.com";

    private final RestClient restClient;

    public FanGraphsClient(RestClient.Builder builder) {
        this.restClient = builder.baseUrl(FANGRAPHS_BASE_URL).build();
    }

    @CircuitBreaker(name = "fangraphs", fallbackMethod = "fetchBattingLeaderboardFallback")
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> fetchBattingLeaderboard(int season) {
        log.info("Fetching FanGraphs batting leaderboard for season {}", season);
        Map<String, Object> response = restClient.get()
                .uri("/api/leaders/major-league/index?pos=all&stats=bat&lg=all&qual=y&season={season}&season1={season}&ind=0&type=8&month=0&team=0&pageitems=2000",
                        season, season)
                .retrieve()
                .body(Map.class);
        if (response == null) return List.of();
        Object data = response.get("data");
        return data instanceof List ? (List<Map<String, Object>>) data : List.of();
    }

    @SuppressWarnings("unused")
    private List<Map<String, Object>> fetchBattingLeaderboardFallback(int season, Throwable t) {
        log.warn("FanGraphs batting leaderboard circuit breaker open for season {}: {}", season, t.getMessage());
        return Collections.emptyList();
    }

    @CircuitBreaker(name = "fangraphs", fallbackMethod = "fetchPitchingLeaderboardFallback")
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> fetchPitchingLeaderboard(int season) {
        log.info("Fetching FanGraphs pitching leaderboard for season {}", season);
        Map<String, Object> response = restClient.get()
                .uri("/api/leaders/major-league/index?pos=all&stats=pit&lg=all&qual=y&season={season}&season1={season}&ind=0&type=8&month=0&team=0&pageitems=2000",
                        season, season)
                .retrieve()
                .body(Map.class);
        if (response == null) return List.of();
        Object data = response.get("data");
        return data instanceof List ? (List<Map<String, Object>>) data : List.of();
    }

    @SuppressWarnings("unused")
    private List<Map<String, Object>> fetchPitchingLeaderboardFallback(int season, Throwable t) {
        log.warn("FanGraphs pitching leaderboard circuit breaker open for season {}: {}", season, t.getMessage());
        return Collections.emptyList();
    }
}
