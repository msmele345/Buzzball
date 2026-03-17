package com.buzzball.integration;

import com.azure.spring.cloud.autoconfigure.implementation.cosmos.AzureCosmosAutoConfiguration;
import com.azure.spring.cloud.autoconfigure.implementation.data.cosmos.CosmosDataAutoConfiguration;
import com.azure.spring.cloud.autoconfigure.implementation.data.cosmos.CosmosRepositoriesAutoConfiguration;
import com.buzzball.model.Team;
import com.buzzball.repository.*;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@EnableAutoConfiguration(exclude = {
        AzureCosmosAutoConfiguration.class,
        CosmosDataAutoConfiguration.class,
        CosmosRepositoriesAutoConfiguration.class
})
class TeamIntegrationTest {

    @TestConfiguration
    static class FixCsvConverterConfig implements WebMvcConfigurer {
        // Remove any HttpMessageConverter backed by CsvMapper, which Spring Boot
        // auto-registers because CsvMapper extends ObjectMapper.
        @Override
        public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
            converters.removeIf(c -> {
                if (c instanceof MappingJackson2HttpMessageConverter jacksonConverter) {
                    return jacksonConverter.getObjectMapper() instanceof CsvMapper;
                }
                return false;
            });
        }
    }

    @Autowired
    private MockMvc mockMvc;

    // All CosmosRepository beans must be mocked to prevent Cosmos auto-config failures
    @MockBean
    private TeamRepository teamRepository;
    @MockBean
    private PlayerRepository playerRepository;
    @MockBean
    private BattingStatsRepository battingStatsRepository;
    @MockBean
    private PitchingStatsRepository pitchingStatsRepository;
    @MockBean
    private FieldingStatsRepository fieldingStatsRepository;
    @MockBean
    private MatchupProjectionRepository matchupProjectionRepository;

    @Test
    void getAllTeams_fullFlow_200() throws Exception {
        Team yankees = buildTeam("nyy", "Yankees", "NYY", "AL East", "AL", 50, 30);
        Team redSox = buildTeam("bos", "Red Sox", "BOS", "AL East", "AL", 45, 35);
        when(teamRepository.findAll()).thenReturn(List.of(yankees, redSox));

        mockMvc.perform(get("/api/v1/teams")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].teamId").value("nyy"))
                .andExpect(jsonPath("$[0].name").value("Yankees"))
                .andExpect(jsonPath("$[0].abbreviation").value("NYY"))
                .andExpect(jsonPath("$[0].division").value("AL East"))
                .andExpect(jsonPath("$[0].league").value("AL"))
                .andExpect(jsonPath("$[0].wins").value(50))
                .andExpect(jsonPath("$[0].losses").value(30))
                .andExpect(jsonPath("$[0].winPct").value(0.625))
                .andExpect(jsonPath("$[0].gamesBack").value(0.0))
                .andExpect(jsonPath("$[0].runDifferential").value(75))
                .andExpect(jsonPath("$[1].teamId").value("bos"))
                .andExpect(jsonPath("$[1].name").value("Red Sox"));
    }

    @Test
    void getTeam_fullFlow_200() throws Exception {
        Team yankees = buildTeam("nyy", "Yankees", "NYY", "AL East", "AL", 50, 30);
        when(teamRepository.findById("nyy")).thenReturn(Optional.of(yankees));

        mockMvc.perform(get("/api/v1/teams/nyy")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.teamId").value("nyy"))
                .andExpect(jsonPath("$.name").value("Yankees"))
                .andExpect(jsonPath("$.abbreviation").value("NYY"))
                .andExpect(jsonPath("$.division").value("AL East"))
                .andExpect(jsonPath("$.league").value("AL"))
                .andExpect(jsonPath("$.wins").value(50))
                .andExpect(jsonPath("$.losses").value(30))
                .andExpect(jsonPath("$.winPct").value(0.625))
                .andExpect(jsonPath("$.runDifferential").value(75));
    }

    @Test
    void getTeam_notFound_500() throws Exception {
        // Verifies current behavior: TeamService throws generic RuntimeException,
        // GlobalExceptionHandler maps to 500 ProblemDetail.
        // Will change to 404 when dedicated TeamNotFoundException is introduced.
        when(teamRepository.findById("unknown")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/teams/unknown")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.title").value("Internal Server Error"))
                .andExpect(jsonPath("$.detail").value("Team not found: unknown"));
    }

    private Team buildTeam(String id, String name, String abbrev, String division,
                           String league, int wins, int losses) {
        return Team.builder()
                .teamId(id)
                .name(name)
                .abbreviation(abbrev)
                .division(division)
                .league(league)
                .venue("Stadium")
                .wins(wins)
                .losses(losses)
                .winPct((double) wins / (wins + losses))
                .gamesBack(0.0)
                .runsScored(400)
                .runsAllowed(325)
                .runDifferential(75)
                .build();
    }
}
