package com.buzzball.integration;

import com.buzzball.model.BattingStats;
import com.buzzball.model.PitchingStats;
import com.buzzball.model.Player;
import com.buzzball.model.Team;
import com.buzzball.service.ingestion.DataIngestionOrchestrator;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.List;
import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class DataIngestionOrchestratorIntegrationTest extends BaseIntegrationTest {

    @RegisterExtension
    static WireMockExtension wireMock = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    @Autowired
    private DataIngestionOrchestrator orchestrator;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("buzzball.clients.statcast.base-url", wireMock::baseUrl);
        registry.add("buzzball.clients.fangraphs.base-url", wireMock::baseUrl);
        registry.add("buzzball.clients.mlb.base-url", wireMock::baseUrl);
    }

    @BeforeEach
    void resetStubs() {
        wireMock.resetAll();
        reset(battingStatsRepository, pitchingStatsRepository, playerRepository, teamRepository);
    }

    // ── Statcast Batting ────────────────────────────────────────────────

    @Test
    void refreshStatcastData_persistsBattingStatsFromCsv() {
        stubStatcastBatting(2025, "stubs/statcast_batting.csv");
        stubStatcastPitching(2025, "stubs/statcast_pitching.csv");
        when(battingStatsRepository.findById(anyString())).thenReturn(Optional.empty());
        when(pitchingStatsRepository.findById(anyString())).thenReturn(Optional.empty());

        orchestrator.refreshStatcastData(2025);

        ArgumentCaptor<BattingStats> captor = ArgumentCaptor.forClass(BattingStats.class);
        verify(battingStatsRepository, atLeast(3)).save(captor.capture());

        List<BattingStats> saved = captor.getAllValues();
        assertThat(saved).extracting(BattingStats::getPlayerId)
                .contains("592450", "660271", "646240");

        BattingStats judge = saved.stream()
                .filter(s -> "592450".equals(s.getPlayerId())).findFirst().orElseThrow();
        assertThat(judge.getId()).isEqualTo("592450-2025");
        assertThat(judge.getSeason()).isEqualTo(2025);
    }

    @Test
    void refreshStatcastData_persistsPitchingStatsFromCsv() {
        stubStatcastBatting(2025, "stubs/statcast_batting.csv");
        stubStatcastPitching(2025, "stubs/statcast_pitching.csv");
        when(battingStatsRepository.findById(anyString())).thenReturn(Optional.empty());
        when(pitchingStatsRepository.findById(anyString())).thenReturn(Optional.empty());

        orchestrator.refreshStatcastData(2025);

        ArgumentCaptor<PitchingStats> captor = ArgumentCaptor.forClass(PitchingStats.class);
        verify(pitchingStatsRepository, atLeast(2)).save(captor.capture());

        List<PitchingStats> saved = captor.getAllValues();
        assertThat(saved).extracting(PitchingStats::getPlayerId)
                .contains("669373", "676979");

        PitchingStats skubal = saved.stream()
                .filter(s -> "669373".equals(s.getPlayerId())).findFirst().orElseThrow();
        assertThat(skubal.getId()).isEqualTo("669373-2025");
        assertThat(skubal.getXera()).isNotNull();
    }

    @Test
    void refreshStatcastData_mergesIntoExistingBattingStats() {
        stubStatcastBatting(2025, "stubs/statcast_batting.csv");
        stubStatcastPitching(2025, "stubs/statcast_pitching.csv");

        BattingStats existing = BattingStats.builder()
                .id("592450-2025").playerId("592450").season(2025)
                .woba(0.463).fWar(10.1)
                .build();
        when(battingStatsRepository.findById("592450-2025")).thenReturn(Optional.of(existing));
        when(battingStatsRepository.findById(argThat(id -> !"592450-2025".equals(id)))).thenReturn(Optional.empty());
        when(pitchingStatsRepository.findById(anyString())).thenReturn(Optional.empty());

        orchestrator.refreshStatcastData(2025);

        ArgumentCaptor<BattingStats> captor = ArgumentCaptor.forClass(BattingStats.class);
        verify(battingStatsRepository, atLeast(1)).save(captor.capture());

        BattingStats merged = captor.getAllValues().stream()
                .filter(s -> "592450".equals(s.getPlayerId())).findFirst().orElseThrow();
        // FanGraphs fields preserved from existing record
        assertThat(merged.getWoba()).isEqualTo(0.463);
        assertThat(merged.getFWar()).isEqualTo(10.1);
    }

    @Test
    void refreshStatcastData_fallsBackToPreviousSeason_whenCurrentSeasonEmpty() {
        // 2026 returns empty CSV
        wireMock.stubFor(get(urlPathEqualTo("/leaderboard/expected_statistics"))
                .withQueryParam("type", equalTo("batter"))
                .withQueryParam("year", equalTo("2026"))
                .willReturn(ok("")));
        // 2025 returns data
        stubStatcastBatting(2025, "stubs/statcast_batting.csv");
        // Pitching for both
        wireMock.stubFor(get(urlPathEqualTo("/leaderboard/expected_statistics"))
                .withQueryParam("type", equalTo("pitcher"))
                .withQueryParam("year", equalTo("2026"))
                .willReturn(ok("")));
        stubStatcastPitching(2025, "stubs/statcast_pitching.csv");
        when(battingStatsRepository.findById(anyString())).thenReturn(Optional.empty());
        when(pitchingStatsRepository.findById(anyString())).thenReturn(Optional.empty());

        orchestrator.refreshStatcastData(2026);

        verify(battingStatsRepository, atLeast(3)).save(any(BattingStats.class));
        // Verify the fallback actually requested 2025
        wireMock.verify(getRequestedFor(urlPathEqualTo("/leaderboard/expected_statistics"))
                .withQueryParam("year", equalTo("2025"))
                .withQueryParam("type", equalTo("batter")));
    }

    // ── FanGraphs Batting ───────────────────────────────────────────────

    @Test
    void refreshFanGraphsData_persistsBattingStatsFromJson() {
        stubFanGraphsBatting(2025, "stubs/fangraphs_batting.json");
        stubFanGraphsPitching(2025, "stubs/fangraphs_pitching.json");
        when(battingStatsRepository.findById(anyString())).thenReturn(Optional.empty());
        when(pitchingStatsRepository.findById(anyString())).thenReturn(Optional.empty());

        orchestrator.refreshFanGraphsData(2025);

        ArgumentCaptor<BattingStats> captor = ArgumentCaptor.forClass(BattingStats.class);
        verify(battingStatsRepository, times(3)).save(captor.capture());

        List<BattingStats> saved = captor.getAllValues();
        assertThat(saved).extracting(BattingStats::getPlayerId)
                .containsExactlyInAnyOrder("592450", "660271", "646240");

        BattingStats judge = saved.stream()
                .filter(s -> "592450".equals(s.getPlayerId())).findFirst().orElseThrow();
        assertThat(judge.getWoba()).isEqualTo(0.463);
        assertThat(judge.getWrcPlus()).isEqualTo(204.5);
        assertThat(judge.getFWar()).isEqualTo(10.1);
        assertThat(judge.getBabip()).isEqualTo(0.376);
    }

    @Test
    void refreshFanGraphsData_persistsPitchingStatsFromJson() {
        stubFanGraphsBatting(2025, "stubs/fangraphs_batting.json");
        stubFanGraphsPitching(2025, "stubs/fangraphs_pitching.json");
        when(battingStatsRepository.findById(anyString())).thenReturn(Optional.empty());
        when(pitchingStatsRepository.findById(anyString())).thenReturn(Optional.empty());

        orchestrator.refreshFanGraphsData(2025);

        ArgumentCaptor<PitchingStats> captor = ArgumentCaptor.forClass(PitchingStats.class);
        verify(pitchingStatsRepository, times(2)).save(captor.capture());

        List<PitchingStats> saved = captor.getAllValues();
        assertThat(saved).extracting(PitchingStats::getPlayerId)
                .containsExactlyInAnyOrder("669373", "676979");

        PitchingStats skubal = saved.stream()
                .filter(s -> "669373".equals(s.getPlayerId())).findFirst().orElseThrow();
        assertThat(skubal.getFip()).isEqualTo(2.449);
        assertThat(skubal.getXfip()).isEqualTo(2.664);
        assertThat(skubal.getFWar()).isEqualTo(6.6);
        assertThat(skubal.getBabip()).isEqualTo(0.273);
    }

    @Test
    void refreshFanGraphsData_mergesIntoExistingPitchingStats() {
        stubFanGraphsBatting(2025, "stubs/fangraphs_batting.json");
        stubFanGraphsPitching(2025, "stubs/fangraphs_pitching.json");

        PitchingStats existing = PitchingStats.builder()
                .id("669373-2025").playerId("669373").season(2025)
                .xera(2.45).whiffPct(35.0)
                .build();
        when(pitchingStatsRepository.findById("669373-2025")).thenReturn(Optional.of(existing));
        when(pitchingStatsRepository.findById(argThat(id -> !"669373-2025".equals(id)))).thenReturn(Optional.empty());
        when(battingStatsRepository.findById(anyString())).thenReturn(Optional.empty());

        orchestrator.refreshFanGraphsData(2025);

        ArgumentCaptor<PitchingStats> captor = ArgumentCaptor.forClass(PitchingStats.class);
        verify(pitchingStatsRepository, atLeast(1)).save(captor.capture());

        PitchingStats merged = captor.getAllValues().stream()
                .filter(s -> "669373".equals(s.getPlayerId())).findFirst().orElseThrow();
        // Statcast fields preserved from existing record
        assertThat(merged.getXera()).isEqualTo(2.45);
        assertThat(merged.getWhiffPct()).isEqualTo(35.0);
        // FanGraphs fields updated
        assertThat(merged.getFip()).isEqualTo(2.449);
        assertThat(merged.getFWar()).isEqualTo(6.6);
    }

    @Test
    void refreshFanGraphsData_fallsBackToPreviousSeason_whenCurrentSeasonEmpty() {
        // 2026 returns empty data
        wireMock.stubFor(get(urlPathEqualTo("/api/leaders/major-league/data"))
                .withQueryParam("season", equalTo("2026"))
                .withQueryParam("stats", equalTo("bat"))
                .willReturn(okJson("{\"data\":[]}")));
        wireMock.stubFor(get(urlPathEqualTo("/api/leaders/major-league/data"))
                .withQueryParam("season", equalTo("2026"))
                .withQueryParam("stats", equalTo("pit"))
                .willReturn(okJson("{\"data\":[]}")));
        // 2025 returns data
        stubFanGraphsBatting(2025, "stubs/fangraphs_batting.json");
        stubFanGraphsPitching(2025, "stubs/fangraphs_pitching.json");
        when(battingStatsRepository.findById(anyString())).thenReturn(Optional.empty());
        when(pitchingStatsRepository.findById(anyString())).thenReturn(Optional.empty());

        orchestrator.refreshFanGraphsData(2026);

        verify(battingStatsRepository, atLeast(3)).save(any(BattingStats.class));
        wireMock.verify(getRequestedFor(urlPathEqualTo("/api/leaders/major-league/data"))
                .withQueryParam("season", equalTo("2025"))
                .withQueryParam("stats", equalTo("bat")));
    }

    @Test
    void refreshFanGraphsData_skipsRowsWithMissingPlayerId() {
        String json = """
                {"data":[
                  {"playerid":15640,"xMLBAMID":592450,"wOBA":0.463,"wRC+":204.5,"WAR":10.1,"BABIP":0.376},
                  {"playerid":99999,"wOBA":0.300,"wRC+":100.0,"WAR":2.0,"BABIP":0.290},
                  {"playerid":88888,"xMLBAMID":"null","wOBA":0.310,"wRC+":105.0,"WAR":2.5,"BABIP":0.300}
                ],"totalCount":3}""";
        wireMock.stubFor(get(urlPathEqualTo("/api/leaders/major-league/data"))
                .withQueryParam("stats", equalTo("bat"))
                .willReturn(okJson(json)));
        wireMock.stubFor(get(urlPathEqualTo("/api/leaders/major-league/data"))
                .withQueryParam("stats", equalTo("pit"))
                .willReturn(okJson("{\"data\":[]}")));
        when(battingStatsRepository.findById(anyString())).thenReturn(Optional.empty());

        orchestrator.refreshFanGraphsData(2025);

        // Only the row with valid xMLBAMID=592450 should be saved
        verify(battingStatsRepository, times(1)).save(any(BattingStats.class));
    }

    // ── Standings ───────────────────────────────────────────────────────

    @Test
    void refreshStandings_persistsTeamsFromMlbApi() {
        wireMock.stubFor(get(urlPathEqualTo("/standings"))
                .willReturn(okJson(loadStub("stubs/mlb_standings.json"))));

        orchestrator.refreshStandings();

        ArgumentCaptor<Team> captor = ArgumentCaptor.forClass(Team.class);
        verify(teamRepository, times(2)).save(captor.capture());

        List<Team> saved = captor.getAllValues();
        assertThat(saved).extracting(Team::getTeamId)
                .containsExactlyInAnyOrder("147", "111");

        Team yankees = saved.stream()
                .filter(t -> "147".equals(t.getTeamId())).findFirst().orElseThrow();
        assertThat(yankees.getName()).isEqualTo("New York Yankees");
        assertThat(yankees.getAbbreviation()).isEqualTo("NYY");
        assertThat(yankees.getWins()).isEqualTo(95);
        assertThat(yankees.getLosses()).isEqualTo(67);
        assertThat(yankees.getDivision()).isEqualTo("American League East");
        assertThat(yankees.getLeague()).isEqualTo("AL");
    }

    // ── Rosters ─────────────────────────────────────────────────────────

    @Test
    void refreshRostersAndStandings_persistsPlayersFromMlbApi() {
        // Stub roster for all 30 teams — only team 147 returns players, rest return empty
        wireMock.stubFor(get(urlPathMatching("/teams/.*/roster"))
                .willReturn(okJson("{\"roster\":[]}")));
        wireMock.stubFor(get(urlPathEqualTo("/teams/147/roster"))
                .willReturn(okJson(loadStub("stubs/mlb_roster_147.json"))));

        orchestrator.refreshRostersAndStandings();

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<Player>> captor = ArgumentCaptor.forClass(List.class);
        verify(playerRepository, atLeast(1)).saveAll(captor.capture());

        List<Player> allSaved = captor.getAllValues().stream()
                .flatMap(List::stream).toList();

        List<Player> yankeePlayers = allSaved.stream()
                .filter(p -> "147".equals(p.getTeamId())).toList();
        assertThat(yankeePlayers).hasSize(2);
        assertThat(yankeePlayers).extracting(Player::getName)
                .containsExactlyInAnyOrder("Aaron Judge", "Giancarlo Stanton");

        Player judge = yankeePlayers.stream()
                .filter(p -> "Aaron Judge".equals(p.getName())).findFirst().orElseThrow();
        assertThat(judge.getPlayerId()).isEqualTo("592450");
        assertThat(judge.getPosition()).isEqualTo("Outfielder");
        assertThat(judge.getPositionAbbrev()).isEqualTo("RF");
        assertThat(judge.getJerseyNumber()).isEqualTo("99");
        assertThat(judge.isActive()).isTrue();
    }

    // ── Statcast empty data ─────────────────────────────────────────────

    @Test
    void refreshStatcastData_handlesEmptyCsvGracefully() {
        wireMock.stubFor(get(urlPathEqualTo("/leaderboard/expected_statistics"))
                .willReturn(ok("")));

        orchestrator.refreshStatcastData(2020);

        verify(battingStatsRepository, never()).save(any(BattingStats.class));
        verify(pitchingStatsRepository, never()).save(any(PitchingStats.class));
    }

    // ── Helpers ──────────────────────────────────────────────────────────

    private void stubStatcastBatting(int season, String stubFile) {
        wireMock.stubFor(get(urlPathEqualTo("/leaderboard/expected_statistics"))
                .withQueryParam("type", equalTo("batter"))
                .withQueryParam("year", equalTo(String.valueOf(season)))
                .willReturn(ok(loadStub(stubFile))));
    }

    private void stubStatcastPitching(int season, String stubFile) {
        wireMock.stubFor(get(urlPathEqualTo("/leaderboard/expected_statistics"))
                .withQueryParam("type", equalTo("pitcher"))
                .withQueryParam("year", equalTo(String.valueOf(season)))
                .willReturn(ok(loadStub(stubFile))));
    }

    private void stubFanGraphsBatting(int season, String stubFile) {
        wireMock.stubFor(get(urlPathEqualTo("/api/leaders/major-league/data"))
                .withQueryParam("stats", equalTo("bat"))
                .withQueryParam("season", equalTo(String.valueOf(season)))
                .willReturn(okJson(loadStub(stubFile))));
    }

    private void stubFanGraphsPitching(int season, String stubFile) {
        wireMock.stubFor(get(urlPathEqualTo("/api/leaders/major-league/data"))
                .withQueryParam("stats", equalTo("pit"))
                .withQueryParam("season", equalTo(String.valueOf(season)))
                .willReturn(okJson(loadStub(stubFile))));
    }

    private String loadStub(String path) {
        try {
            return new String(getClass().getClassLoader().getResourceAsStream(path).readAllBytes());
        } catch (Exception e) {
            throw new RuntimeException("Failed to load stub: " + path, e);
        }
    }
}
