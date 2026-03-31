package com.buzzball.service;

import com.buzzball.dto.LeagueLeaderDto;
import com.buzzball.dto.TrendingPlayerDto;
import com.buzzball.fixtures.BStatsTestProvider;
import com.buzzball.fixtures.PlayerFixtures;
import com.buzzball.mapper.PlayerMapper;
import com.buzzball.mapper.TeamMapper;
import com.buzzball.repository.BattingStatsRepository;
import com.buzzball.repository.PitchingStatsRepository;
import com.buzzball.repository.PlayerRepository;
import com.buzzball.repository.TeamRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private BattingStatsRepository battingStatsRepository;

    @Mock
    private PitchingStatsRepository pitchingStatsRepository;

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private TeamMapper teamMapper;

    @Mock
    private PlayerMapper playerMapper;

    @InjectMocks
    private DashboardService dashboardService;

    private BStatsTestProvider bStatsTestData = BStatsTestProvider.INSTANCE;

    @Test
    void getTrendingHitters_returnsPlayersSortedByFWar() {
        when(battingStatsRepository.findAll()).thenReturn(bStatsTestData.all());
        when(playerRepository.findById("p1")).thenReturn(Optional.of(PlayerFixtures.JUDGE));
        when(playerRepository.findById("p2")).thenReturn(Optional.of(PlayerFixtures.DEVERS));
        when(playerRepository.findById("p3")).thenReturn(Optional.of(PlayerFixtures.UTILITY));

        List<TrendingPlayerDto> result = dashboardService.getTrendingHitters();

        assertThat(result).hasSize(3);
        assertThat(result.get(0).getCurrentWar()).isEqualTo(7.2); // Judge highest fWar
        assertThat(result.get(1).getCurrentWar()).isEqualTo(4.1); // Devers
        assertThat(result.get(2).getCurrentWar()).isEqualTo(0.9); // Utility
    }

    @Test
    void getTrendingHitters_returnsEmptyWhenNoStats() {
        when(battingStatsRepository.findAll()).thenReturn(List.of());

        List<TrendingPlayerDto> result = dashboardService.getTrendingHitters();

        assertThat(result).isEmpty();
    }

    @Test
    void getLeagueLeaders_returnsPlayerName() {
        when(battingStatsRepository.findAll()).thenReturn(List.of(bStatsTestData.elitePowerHitter()));
        when(playerRepository.findById("p1")).thenReturn(Optional.of(PlayerFixtures.JUDGE));

        List<LeagueLeaderDto> actual = dashboardService.getLeagueLeaders();

        assertThat(actual.getFirst().getPlayerName()).isEqualTo("Aaron Judge");
    }
}