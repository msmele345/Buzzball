package com.buzzball.service;

import com.buzzball.dto.PlayerSummaryDto;
import com.buzzball.mapper.PlayerMapper;
import com.buzzball.model.Player;
import com.buzzball.repository.BattingStatsRepository;
import com.buzzball.repository.FieldingStatsRepository;
import com.buzzball.repository.PitchingStatsRepository;
import com.buzzball.repository.PlayerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class PlayerServiceTest {

    @Mock
    private PlayerRepository playerRepository;
    @Mock
    private BattingStatsRepository battingStatsRepository;
    @Mock
    private PitchingStatsRepository pitchingStatsRepository;
    @Mock
    private FieldingStatsRepository fieldingStatsRepository;
    @Mock
    private PlayerMapper playerMapper;

    @InjectMocks
    private PlayerService playerService;

    @Test
    void getAllActivePlayers() {
        when(playerRepository.findByActiveTrue()).thenReturn(List.of(new Player()));
        PlayerSummaryDto playerDto = PlayerSummaryDto.builder().build();
        when(playerMapper.toSummaryDto(any())).thenReturn(playerDto);

        List<PlayerSummaryDto> actual =
                playerService.getAllActivePlayers();

        List<PlayerSummaryDto> expected = List.of(playerDto);
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void getPlayerDetail() {
    }

    @Test
    void getBattingStats() {
    }

    @Test
    void getPitchingStats() {
    }

    @Test
    void getFieldingStats() {
    }

    @Test
    void getPlayersByTeam() {
    }
}