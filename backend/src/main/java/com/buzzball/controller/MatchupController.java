package com.buzzball.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Matchup projection controller — full implementation in Phase 5.
 */
@RestController
@RequestMapping("/api/v1/matchups")
public class MatchupController {

    @GetMapping("/{playerId}/upcoming")
    public ResponseEntity<List<Map<String, Object>>> getUpcomingMatchups(
            @PathVariable String playerId) {
        // Phase 5: returns MatchupProjection results from Cosmos DB
        return ResponseEntity.ok(List.of());
    }
}
