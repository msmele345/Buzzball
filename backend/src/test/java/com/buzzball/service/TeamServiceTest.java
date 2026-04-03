package com.buzzball.service;

import com.buzzball.dto.TeamSummaryDto;
import com.buzzball.fixtures.TeamFixtures;
import com.buzzball.mapper.TeamMapper;
import com.buzzball.model.Team;
import com.buzzball.repository.TeamRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private TeamMapper teamMapper;

    @InjectMocks
    private TeamService teamService;

    @Test
    void getAllTeams_excludesStaleDocuments() {
        List<Team> validTeams = List.of(TeamFixtures.YANKEES, TeamFixtures.RED_SOX);
        when(teamRepository.findAllByDivisionIsNotNull()).thenReturn(validTeams);
        when(teamMapper.toSummaryDto(any(Team.class))).thenReturn(
                TeamSummaryDto.builder().teamId("nyy").build(),
                TeamSummaryDto.builder().teamId("bos").build()
        );

        List<TeamSummaryDto> actual = teamService.getAllTeams();

        assertThat(actual).hasSize(2);
    }

    @Test
    void getTeam_returnsTeamWhenFound() {
        when(teamRepository.findByTeamIdAndDivisionIsNotNull("nyy"))
                .thenReturn(List.of(TeamFixtures.YANKEES));
        TeamSummaryDto expectedDto = TeamSummaryDto.builder()
                .teamId("nyy").name("New York Yankees").wins(95).losses(67).build();
        when(teamMapper.toSummaryDto(TeamFixtures.YANKEES)).thenReturn(expectedDto);

        TeamSummaryDto result = teamService.getTeam("nyy");

        assertThat(result.getTeamId()).isEqualTo("nyy");
        assertThat(result.getName()).isEqualTo("New York Yankees");
        assertThat(result.getWins()).isEqualTo(95);
        assertThat(result.getLosses()).isEqualTo(67);
    }

    @Test
    void getTeam_throwsWhenNotFound() {
        when(teamRepository.findByTeamIdAndDivisionIsNotNull("unknown"))
                .thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> teamService.getTeam("unknown"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Team not found: unknown");
    }
}