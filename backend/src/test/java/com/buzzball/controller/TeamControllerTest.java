package com.buzzball.controller;

import com.buzzball.dto.PlayerSummaryDto;
import com.buzzball.dto.TeamComparisonDto;
import com.buzzball.dto.TeamSummaryDto;
import com.buzzball.service.PlayerService;
import com.buzzball.service.TeamService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TeamControllerTest {

    private TeamService teamService;
    private PlayerService playerService;
    private TeamController teamController;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        teamService = mock(TeamService.class);
        playerService = mock(PlayerService.class);
        teamController = new TeamController(teamService, playerService);
        mockMvc = MockMvcBuilders.standaloneSetup(teamController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getAllTeams_returnsList_200() throws Exception {
        List<TeamSummaryDto> teams = List.of(buildTeamDto("NYY", "Yankees"), buildTeamDto("BOS", "Red Sox"));
        when(teamService.getAllTeams()).thenReturn(teams);

        mockMvc.perform(get("/api/v1/teams").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(teams)));
    }

    @Test
    void getAllTeams_emptyList_200() throws Exception {
        when(teamService.getAllTeams()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/teams").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void getTeam_exists_200() throws Exception {
        TeamSummaryDto team = buildTeamDto("NYY", "Yankees");
        when(teamService.getTeam("nyy")).thenReturn(team);

        mockMvc.perform(get("/api/v1/teams/nyy").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(team)));
    }

    @Test
    void getTeam_notFound_500() throws Exception {
        when(teamService.getTeam("unknown")).thenThrow(new RuntimeException("Team not found: unknown"));

        mockMvc.perform(get("/api/v1/teams/unknown").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getRoster_returnsList_200() throws Exception {
        List<PlayerSummaryDto> players = List.of(
                buildPlayerDto("p1", "Aaron Judge"),
                buildPlayerDto("p2", "Juan Soto")
        );
        when(playerService.getPlayersByTeam("nyy")).thenReturn(players);

        mockMvc.perform(get("/api/v1/teams/nyy/roster").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(players)));
    }

    @Test
    void getRoster_emptyRoster_200() throws Exception {
        when(playerService.getPlayersByTeam("nyy")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/teams/nyy/roster").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void compareTeams_returnsComparison_200() throws Exception {
        TeamSummaryDto nyy = buildTeamDto("NYY", "Yankees");
        TeamSummaryDto bos = buildTeamDto("BOS", "Red Sox");
        TeamComparisonDto comparison = TeamComparisonDto.builder()
                .teams(List.of(nyy, bos))
                .metrics(List.of(
                        TeamComparisonDto.ComparisonMetric.builder()
                                .metricName("wins").label("Wins").values(List.of(50.0, 50.0)).build(),
                        TeamComparisonDto.ComparisonMetric.builder()
                                .metricName("losses").label("Losses").values(List.of(30.0, 30.0)).build()
                ))
                .build();
        when(teamService.compareTeams(List.of("nyy", "bos"))).thenReturn(comparison);

        mockMvc.perform(get("/api/v1/teams/compare")
                        .param("teamIds", "nyy", "bos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(comparison)));
    }

    private PlayerSummaryDto buildPlayerDto(String id, String name) {
        return PlayerSummaryDto.builder()
                .playerId(id)
                .name(name)
                .position("Outfield")
                .positionAbbrev("OF")
                .teamId("nyy")
                .active(true)
                .jerseyNumber("99")
                .build();
    }

    private TeamSummaryDto buildTeamDto(String abbreviation, String name) {
        return TeamSummaryDto.builder()
                .teamId(abbreviation.toLowerCase())
                .name(name)
                .abbreviation(abbreviation)
                .division("AL East")
                .league("AL")
                .wins(50)
                .losses(30)
                .winPct(0.625)
                .gamesBack(0.0)
                .runDifferential(75)
                .build();
    }
}
