package com.buzzball.controller;

import com.buzzball.dto.*;
import com.buzzball.service.PlayerService;
import com.buzzball.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;
    private final PlayerService playerService;

    @GetMapping
    public ResponseEntity<List<TeamSummaryDto>> getAllTeams() {
        return ResponseEntity.ok(teamService.getAllTeams());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamSummaryDto> getTeam(@PathVariable String id) {
        return ResponseEntity.ok(teamService.getTeam(id));
    }

    @GetMapping("/{id}/roster")
    public ResponseEntity<List<PlayerSummaryDto>> getRoster(@PathVariable String id) {
        return ResponseEntity.ok(playerService.getPlayersByTeam(id));
    }

    @GetMapping("/compare")
    public ResponseEntity<TeamComparisonDto> compareTeams(
            @RequestParam List<String> teamIds) {
        return ResponseEntity.ok(teamService.compareTeams(teamIds));
    }
}
