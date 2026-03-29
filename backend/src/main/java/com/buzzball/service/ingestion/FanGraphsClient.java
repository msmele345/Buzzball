package com.buzzball.service.ingestion;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class FanGraphsClient {

    private final RestClient restClient;

    public FanGraphsClient(RestClient.Builder builder,
                           @Value("${buzzball.clients.fangraphs.base-url:https://www.fangraphs.com}") String baseUrl) {
        this.restClient = builder.baseUrl(baseUrl).build();
    }

    @CircuitBreaker(name = "fangraphs-batting", fallbackMethod = "fetchBattingLeaderboardFallback")
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> fetchBattingLeaderboard(int season) {
        log.info("Fetching FanGraphs batting leaderboard for season {}", season);
        Map<String, Object> response = restClient.get()
                .uri("/api/leaders/major-league/data?pos=all&stats=bat&lg=all&qual=y&season={season}&season1={season}&ind=0&type=8&month=0&team=0&pageitems=2000",
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

    @CircuitBreaker(name = "fangraphs-pitching", fallbackMethod = "fetchPitchingLeaderboardFallback")
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> fetchPitchingLeaderboard(int season) {
        log.info("Fetching FanGraphs pitching leaderboard for season {}", season);
        Map<String, Object> response = restClient.get()
                .uri("/api/leaders/major-league/data?pos=all&stats=pit&lg=all&qual=y&season={season}&season1={season}&ind=0&type=8&month=0&team=0&pageitems=2000",
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
