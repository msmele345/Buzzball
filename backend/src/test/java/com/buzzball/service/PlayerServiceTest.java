package com.buzzball.service;

import com.buzzball.dto.BattingStatsDto;
import com.buzzball.dto.PlayerDetailDto;
import com.buzzball.dto.PlayerSummaryDto;
import com.buzzball.mapper.PlayerMapper;
import com.buzzball.model.BattingStats;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

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
        Player expected = new Player();
        expected.setPlayerId("!23");
        expected.setName("Bob Bobber");

        when(playerRepository.findById(anyString()))
                .thenReturn(Optional.of(expected));

        PlayerDetailDto expectedDto = PlayerDetailDto.builder().build();
        when(playerMapper.toDetailDto(any()))
                .thenReturn(expectedDto);

        PlayerDetailDto actual = playerService.getPlayerDetail("123");

        assertThat(actual).isEqualTo(expectedDto);

        verify(playerRepository).findById("123");
        verify(playerMapper).toDetailDto(expected);
    }

    @Test
    void getBattingStats() {
        BattingStats bs = BattingStats.builder().build();
        bs.setPlayerId("223");
        bs.setFWar(2.5);
        bs.setId("bs1");
        when(battingStatsRepository.findByPlayerId(anyString()))
                .thenReturn(List.of(bs));

        BattingStatsDto dto = BattingStatsDto.builder().build();
        dto.setFWar(2.5);
        dto.setPlayerId("223");
        when(playerMapper.toBattingStatsDto(any()))
                .thenReturn(dto);

        List<BattingStatsDto> actual = playerService.getBattingStats("223");

        assertThat(actual).usingRecursiveComparison().isEqualTo(List.of(bs));

        verify(battingStatsRepository).findByPlayerId("223");
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