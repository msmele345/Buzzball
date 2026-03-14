package com.buzzball.controller;

import com.buzzball.model.MatchupProjection;
import com.buzzball.service.prediction.MatchupAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/matchups")
@RequiredArgsConstructor
public class MatchupController {

    private final MatchupAnalysisService matchupAnalysisService;

    @GetMapping("/{playerId}/upcoming")
    public ResponseEntity<List<MatchupProjection>> getUpcomingMatchups(
            @PathVariable String playerId) {
        return ResponseEntity.ok(matchupAnalysisService.getUpcomingProjections(playerId));
    }

    @PostMapping("/{playerId}/project")
    public ResponseEntity<MatchupProjection> projectMatchup(
            @PathVariable String playerId,
            @RequestParam String pitcherId,
            @RequestParam String gameDate,
            @RequestParam(defaultValue = "") String venue) {
        return ResponseEntity.ok(
                matchupAnalysisService.projectMatchup(playerId, pitcherId, gameDate, venue));
    }
}
