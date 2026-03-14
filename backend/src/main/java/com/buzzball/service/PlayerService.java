package com.buzzball.service;

import com.buzzball.dto.*;
import com.buzzball.mapper.PlayerMapper;
import com.buzzball.model.*;
import com.buzzball.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final BattingStatsRepository battingStatsRepository;
    private final PitchingStatsRepository pitchingStatsRepository;
    private final FieldingStatsRepository fieldingStatsRepository;
    private final PlayerMapper playerMapper;

    @Cacheable("players-list")
    public List<PlayerSummaryDto> getAllActivePlayers() {
        return playerRepository.findByActiveTrue()
                .stream()
                .map(playerMapper::toSummaryDto)
                .toList();
    }

    @Cacheable(value = "player-detail", key = "#playerId")
    public PlayerDetailDto getPlayerDetail(String playerId) {
        return playerRepository.findById(playerId)
                .map(playerMapper::toDetailDto)
                .orElseThrow(() -> new PlayerNotFoundException(playerId));
    }

    public List<BattingStatsDto> getBattingStats(String playerId) {
        return battingStatsRepository.findByPlayerId(playerId)
                .stream()
                .map(playerMapper::toBattingStatsDto)
                .toList();
    }

    public List<PitchingStatsDto> getPitchingStats(String playerId) {
        return pitchingStatsRepository.findByPlayerId(playerId)
                .stream()
                .map(playerMapper::toPitchingStatsDto)
                .toList();
    }

    public List<FieldingStatsDto> getFieldingStats(String playerId) {
        return fieldingStatsRepository.findByPlayerId(playerId)
                .stream()
                .map(playerMapper::toFieldingStatsDto)
                .toList();
    }

    public List<PlayerSummaryDto> getPlayersByTeam(String teamId) {
        return playerRepository.findByTeamId(teamId)
                .stream()
                .map(playerMapper::toSummaryDto)
                .toList();
    }
}
