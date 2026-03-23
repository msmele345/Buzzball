package com.buzzball.integration;

import com.buzzball.model.BattingStats;
import com.buzzball.model.FieldingStats;
import com.buzzball.model.PitchingStats;
import com.buzzball.model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class PlayerControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void clearCaches() {
        cacheManager.getCacheNames().forEach(name -> cacheManager.getCache(name).clear());
    }

    // ── GET /api/v1/players ──

    @Test
    void getAllPlayers_returnsActivePlayers_200() {
        Player judge = buildPlayer("p1", "Aaron Judge", "Outfield", "OF", "nyy", true, "99");
        Player ohtani = buildPlayer("p2", "Shohei Ohtani", "Designated Hitter", "DH", "lad", true, "17");
        when(playerRepository.findByActiveTrue()).thenReturn(List.of(judge, ohtani));

        ResponseEntity<String> response = restTemplate.getForEntity("/api/v1/players", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        String body = response.getBody();
        assertThat(body).contains("\"playerId\":\"p1\"");
        assertThat(body).contains("\"name\":\"Aaron Judge\"");
        assertThat(body).contains("\"position\":\"Outfield\"");
        assertThat(body).contains("\"positionAbbrev\":\"OF\"");
        assertThat(body).contains("\"teamId\":\"nyy\"");
        assertThat(body).contains("\"jerseyNumber\":\"99\"");
        assertThat(body).contains("\"playerId\":\"p2\"");
        assertThat(body).contains("\"name\":\"Shohei Ohtani\"");
        assertThat(body).contains("\"teamId\":\"lad\"");
    }

    @Test
    void getAllPlayers_noActivePlayers_returnsEmptyList_200() {
        when(playerRepository.findByActiveTrue()).thenReturn(Collections.emptyList());

        ResponseEntity<String> response = restTemplate.getForEntity("/api/v1/players", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("[]");
    }

    // ── GET /api/v1/players/{id} ──

    @Test
    void getPlayer_existingPlayer_200() {
        Player judge = buildPlayerWithDetails("p1", "Aaron Judge", "Outfield", "OF",
                "nyy", true, "99", "1992-04-26", "R/R");
        when(playerRepository.findById("p1")).thenReturn(Optional.of(judge));

        ResponseEntity<String> response = restTemplate.getForEntity("/api/v1/players/p1", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        String body = response.getBody();
        assertThat(body).contains("\"playerId\":\"p1\"");
        assertThat(body).contains("\"name\":\"Aaron Judge\"");
        assertThat(body).contains("\"position\":\"Outfield\"");
        assertThat(body).contains("\"positionAbbrev\":\"OF\"");
        assertThat(body).contains("\"teamId\":\"nyy\"");
        assertThat(body).contains("\"jerseyNumber\":\"99\"");
        assertThat(body).contains("\"birthDate\":\"1992-04-26\"");
        assertThat(body).contains("\"batsThrows\":\"R/R\"");
    }

    @Test
    void getPlayer_notFound_404() {
        when(playerRepository.findById("unknown")).thenReturn(Optional.empty());

        ResponseEntity<String> response = restTemplate.getForEntity("/api/v1/players/unknown", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).contains("Player not found: unknown");
    }

    // ── GET /api/v1/players/{id}/batting ──

    @Test
    void getBattingStats_returnsMultipleSeasons_200() {
        BattingStats stats2024 = BattingStats.builder()
                .id("p1-2024").playerId("p1").season(2024)
                .gamesPlayed(158).atBats(570).hits(180).homeRuns(58).rbi(144)
                .walks(133).strikeouts(175).stolenBases(3)
                .battingAverage(0.316).onBasePercentage(0.425).sluggingPercentage(0.701).ops(1.126)
                .xba(0.305).xslg(0.680).xwoba(0.445)
                .exitVelocityAvg(95.9).barrelPct(26.1).hardHitPct(59.5).launchAngleAvg(14.5)
                .woba(0.460).wrcPlus(210.0).fWar(10.6).babip(0.340)
                .build();
        BattingStats stats2023 = BattingStats.builder()
                .id("p1-2023").playerId("p1").season(2023)
                .gamesPlayed(106).atBats(398).hits(109).homeRuns(37).rbi(75)
                .walks(78).strikeouts(130).stolenBases(2)
                .battingAverage(0.274).onBasePercentage(0.394).sluggingPercentage(0.613).ops(1.007)
                .build();
        when(battingStatsRepository.findByPlayerId("p1")).thenReturn(List.of(stats2024, stats2023));

        ResponseEntity<String> response = restTemplate.getForEntity("/api/v1/players/p1/batting", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        String body = response.getBody();
        assertThat(body).contains("\"season\":2024");
        assertThat(body).contains("\"homeRuns\":58");
        assertThat(body).contains("\"battingAverage\":0.316");
        assertThat(body).contains("\"ops\":1.126");
        assertThat(body).contains("\"xba\":0.305");
        assertThat(body).contains("\"exitVelocityAvg\":95.9");
        assertThat(body).contains("\"barrelPct\":26.1");
        assertThat(body).contains("\"wrcPlus\":210.0");
        assertThat(body).contains("\"season\":2023");
        assertThat(body).contains("\"homeRuns\":37");
    }

    @Test
    void getBattingStats_noStats_returnsEmptyList_200() {
        when(battingStatsRepository.findByPlayerId("p-none")).thenReturn(Collections.emptyList());

        ResponseEntity<String> response = restTemplate.getForEntity("/api/v1/players/p-none/batting", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("[]");
    }

    // ── GET /api/v1/players/{id}/pitching ──

    @Test
    void getPitchingStats_returnsStats_200() {
        PitchingStats stats = PitchingStats.builder()
                .id("p3-2024").playerId("p3").season(2024)
                .gamesPlayed(33).gamesStarted(33).inningsPitched(209.0)
                .era(2.89).wins(18).losses(6).saves(0)
                .strikeouts(250).walks(55).whip(1.02).battingAverageAgainst(0.210)
                .spinRateFastball(2450.0).xera(2.95).whiffPct(32.5).chasePct(35.0)
                .fip(2.75).xfip(2.80).fWar(7.2).babip(0.280)
                .build();
        when(pitchingStatsRepository.findByPlayerId("p3")).thenReturn(List.of(stats));

        ResponseEntity<String> response = restTemplate.getForEntity("/api/v1/players/p3/pitching", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        String body = response.getBody();
        assertThat(body).contains("\"season\":2024");
        assertThat(body).contains("\"gamesStarted\":33");
        assertThat(body).contains("\"inningsPitched\":209.0");
        assertThat(body).contains("\"era\":2.89");
        assertThat(body).contains("\"wins\":18");
        assertThat(body).contains("\"strikeouts\":250");
        assertThat(body).contains("\"whip\":1.02");
        assertThat(body).contains("\"spinRateFastball\":2450.0");
        assertThat(body).contains("\"xera\":2.95");
        assertThat(body).contains("\"whiffPct\":32.5");
        assertThat(body).contains("\"fip\":2.75");
    }

    @Test
    void getPitchingStats_noStats_returnsEmptyList_200() {
        when(pitchingStatsRepository.findByPlayerId("p-batter")).thenReturn(Collections.emptyList());

        ResponseEntity<String> response = restTemplate.getForEntity("/api/v1/players/p-batter/pitching", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("[]");
    }

    // ── GET /api/v1/players/{id}/fielding ──

    @Test
    void getFieldingStats_multiplePositions_200() {
        FieldingStats outfield = FieldingStats.builder()
                .id("p1-OF-2024").playerId("p1").season(2024).position("RF")
                .games(150).putouts(280).assists(8).errors(2).fieldingPct(0.993)
                .oaa(12).drs(15)
                .build();
        FieldingStats dh = FieldingStats.builder()
                .id("p1-DH-2024").playerId("p1").season(2024).position("DH")
                .games(8).putouts(0).assists(0).errors(0).fieldingPct(0.0)
                .oaa(0).drs(0)
                .build();
        when(fieldingStatsRepository.findByPlayerId("p1")).thenReturn(List.of(outfield, dh));

        ResponseEntity<String> response = restTemplate.getForEntity("/api/v1/players/p1/fielding", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        String body = response.getBody();
        assertThat(body).contains("\"position\":\"RF\"");
        assertThat(body).contains("\"games\":150");
        assertThat(body).contains("\"putouts\":280");
        assertThat(body).contains("\"fieldingPct\":0.993");
        assertThat(body).contains("\"oaa\":12");
        assertThat(body).contains("\"drs\":15");
        assertThat(body).contains("\"position\":\"DH\"");
    }

    @Test
    void getFieldingStats_noStats_returnsEmptyList_200() {
        when(fieldingStatsRepository.findByPlayerId("p-rookie")).thenReturn(Collections.emptyList());

        ResponseEntity<String> response = restTemplate.getForEntity("/api/v1/players/p-rookie/fielding", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("[]");
    }

    // ── Repository error propagation ──

    @Test
    void getAllPlayers_repositoryThrows_500() {
        when(playerRepository.findByActiveTrue()).thenThrow(new RuntimeException("Cosmos connection failed"));

        ResponseEntity<String> response = restTemplate.getForEntity("/api/v1/players", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).contains("Cosmos connection failed");
    }

    @Test
    void getBattingStats_repositoryThrows_500() {
        when(battingStatsRepository.findByPlayerId("p1")).thenThrow(new RuntimeException("Read timeout"));

        ResponseEntity<String> response = restTemplate.getForEntity("/api/v1/players/p1/batting", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).contains("Read timeout");
    }

    // ── Helpers ──

    private Player buildPlayer(String id, String name, String position, String positionAbbrev,
                               String teamId, boolean active, String jerseyNumber) {
        return Player.builder()
                .playerId(id).name(name).position(position).positionAbbrev(positionAbbrev)
                .teamId(teamId).active(active).jerseyNumber(jerseyNumber)
                .build();
    }

    private Player buildPlayerWithDetails(String id, String name, String position, String positionAbbrev,
                                          String teamId, boolean active, String jerseyNumber,
                                          String birthDate, String batsThrows) {
        return Player.builder()
                .playerId(id).name(name).position(position).positionAbbrev(positionAbbrev)
                .teamId(teamId).active(active).jerseyNumber(jerseyNumber)
                .birthDate(birthDate).batsThrows(batsThrows)
                .build();
    }
}
