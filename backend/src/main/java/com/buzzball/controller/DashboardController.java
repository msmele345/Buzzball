package com.buzzball.controller;

import com.buzzball.dto.*;
import com.buzzball.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<DashboardDto> getDashboard() {
        return ResponseEntity.ok(dashboardService.getDashboard());
    }

    @GetMapping("/trending")
    public ResponseEntity<List<TrendingPlayerDto>> getTrending(
            @RequestParam(defaultValue = "hitting") String category) {
        if ("pitching".equalsIgnoreCase(category)) {
            return ResponseEntity.ok(dashboardService.getTrendingPitchers());
        }
        return ResponseEntity.ok(dashboardService.getTrendingHitters());
    }

    @GetMapping("/league-leaders")
    public ResponseEntity<List<LeagueLeaderDto>> getLeagueLeaders() {
        return ResponseEntity.ok(dashboardService.getLeagueLeaders());
    }

    @GetMapping("/standings")
    public ResponseEntity<DashboardDto> getStandings() {
        return ResponseEntity.ok(dashboardService.getDashboard());
    }
}
