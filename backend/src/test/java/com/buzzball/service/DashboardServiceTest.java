package com.buzzball.service;

import com.buzzball.dto.LeagueLeaderDto;
import com.buzzball.dto.TeamSummaryDto;
import com.buzzball.dto.TrendingPlayerDto;
import com.buzzball.fixtures.BStatsTestProvider;
import com.buzzball.fixtures.PStatsTestProvider;
import com.buzzball.fixtures.PlayerFixtures;
import com.buzzball.fixtures.TeamFixtures;
import com.buzzball.mapper.PlayerMapper;
import com.buzzball.mapper.TeamMapper;
import com.buzzball.model.Team;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
    private PStatsTestProvider pStatsTestData = PStatsTestProvider.INSTANCE;

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
    void getTrendingPitchers_returnsPitchersSortedByFIP() {
        when(pitchingStatsRepository.findAll())
                .thenReturn(pStatsTestData.all());

        when(playerRepository.findById("p5"))
                .thenReturn(Optional.of(PlayerFixtures.ACE));

        List<TrendingPlayerDto> actual = dashboardService.getTrendingPitchers();

        assertThat(actual.getFirst().getName()).isEqualTo("Gerrit Cole");
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
        assertThat(actual.getFirst().getCategory()).isEqualTo("wOBA");
    }

    @Test
    void getStandingsByLeague_returnsSortedListForGivenLeague() {
        List<Team> teams = List.of(TeamFixtures.YANKEES, TeamFixtures.RED_SOX, TeamFixtures.DODGERS);
        TeamSummaryDto expectedFirstPlaceTeam = TeamSummaryDto.builder()
                .teamId("nyy")
                .name("Yankees")
                .abbreviation("NYY")
                .division("AL East")
                .league("AL")
                .wins(95)
                .losses(5)
                .winPct(55.0)
                .gamesBack(0.0)
                .runDifferential(140)
                .build();

        when(teamRepository.findByLeague("AL")).thenReturn(teams);
        when(teamMapper.toSummaryDto(any())).thenReturn(expectedFirstPlaceTeam,
                TeamSummaryDto.builder().teamId("bos").name("Red Sox").league("AL").winPct(50.0).build(),
                TeamSummaryDto.builder().teamId("lad").name("Dodgers").league("NL").winPct(60.0).build()
        );

        List<TeamSummaryDto> actual = dashboardService.getStandingsByLeague("AL");
        assertThat(actual.size()).isEqualTo(3);
        assertThat(actual.getFirst()).isEqualTo(expectedFirstPlaceTeam);
    }
}