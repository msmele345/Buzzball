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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
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
    void getAllTeams_callsRepo_returns200() {
        List<Team> expectedList = List.of(TeamFixtures.YANKEES);

        when(teamRepository.findAll()).thenReturn(expectedList);

        List<TeamSummaryDto> actual = teamService.getAllTeams();

        assertThat(actual).hasSize(1);
    }
}