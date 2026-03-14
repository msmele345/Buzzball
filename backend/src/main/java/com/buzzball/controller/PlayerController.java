package com.buzzball.controller;

import com.buzzball.dto.*;
import com.buzzball.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/players")
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerService playerService;

    @GetMapping
    public ResponseEntity<List<PlayerSummaryDto>> getAllPlayers() {
        return ResponseEntity.ok(playerService.getAllActivePlayers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlayerDetailDto> getPlayer(@PathVariable String id) {
        return ResponseEntity.ok(playerService.getPlayerDetail(id));
    }

    @GetMapping("/{id}/batting")
    public ResponseEntity<List<BattingStatsDto>> getBattingStats(@PathVariable String id) {
        return ResponseEntity.ok(playerService.getBattingStats(id));
    }

    @GetMapping("/{id}/pitching")
    public ResponseEntity<List<PitchingStatsDto>> getPitchingStats(@PathVariable String id) {
        return ResponseEntity.ok(playerService.getPitchingStats(id));
    }

    @GetMapping("/{id}/fielding")
    public ResponseEntity<List<FieldingStatsDto>> getFieldingStats(@PathVariable String id) {
        return ResponseEntity.ok(playerService.getFieldingStats(id));
    }
}
