package com.buzzball.controller;

import com.buzzball.model.MatchupProjection;
import com.buzzball.service.prediction.MatchupAnalysisService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MatchupControllerTest {

    private MatchupAnalysisService matchupAnalysisService;
    private MatchupController matchupController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        matchupAnalysisService = mock(MatchupAnalysisService.class);
        matchupController = new MatchupController(matchupAnalysisService);
        mockMvc = MockMvcBuilders.standaloneSetup(matchupController).build();
    }

    @Test
    void getUpComingMatchups_returnsListOfMatchUpProjects_200() throws Exception {

        MatchupProjection e1 = new MatchupProjection();
        MatchupProjection e2 = new MatchupProjection();
        MatchupProjection e3 = new MatchupProjection();
        e1.setGameDate("2024-07-01");
        e1.setPlayerId("1234");
        e2.setPlayerId("1234");
        e3.setPlayerId("1234");
        e2.setGameDate("2024-07-02");
        e3.setGameDate("2024-07-03");

        List<MatchupProjection> expectedProjections = List.of(
                e1, e2, e3
        );
        when(matchupAnalysisService.getUpcomingProjections("1234")).thenReturn(expectedProjections);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/matchups/1234/upcoming")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(expectedProjections)));
    }

    @Test
    void matchUpAnalysis_returnsAnalysisResults_forPlayerPitcher_200() throws Exception {
            MatchupProjection expectedProjection = new MatchupProjection();
            expectedProjection.setPlayerId("1234");
            expectedProjection.setProjectedWoba(88.00);
            expectedProjection.setGameDate("2024-07-01");

            when(matchupAnalysisService.projectMatchup("1234", "5678", "2024-07-01", ""))
                    .thenReturn(expectedProjection);

            mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/matchups/1234/project")
                    .param("pitcherId", "5678")
                    .param("batterId", "1234")
                    .param("gameDate", "2024-07-01")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().string(objectMapper.writeValueAsString(expectedProjection)));
    }
}