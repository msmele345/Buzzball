package com.buzzball.integration;

import com.buzzball.model.Player;
import com.buzzball.model.Team;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class TeamIntegrationTest extends BaseIntegrationTest {

    @Test
    void getAllTeams_fullFlow_200() {
        Team yankees = buildTeam("nyy", "Yankees", "NYY", "AL East", "AL", 50, 30);
        Team redSox = buildTeam("bos", "Red Sox", "BOS", "AL East", "AL", 45, 35);
        when(teamRepository.findAll()).thenReturn(List.of(yankees, redSox));

        ResponseEntity<String> response = restTemplate.getForEntity("/api/v1/teams", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        String body = response.getBody();
        assertThat(body).contains("\"teamId\":\"nyy\"");
        assertThat(body).contains("\"name\":\"Yankees\"");
        assertThat(body).contains("\"abbreviation\":\"NYY\"");
        assertThat(body).contains("\"division\":\"AL East\"");
        assertThat(body).contains("\"league\":\"AL\"");
        assertThat(body).contains("\"wins\":50");
        assertThat(body).contains("\"losses\":30");
        assertThat(body).contains("\"winPct\":0.625");
        assertThat(body).contains("\"gamesBack\":0.0");
        assertThat(body).contains("\"runDifferential\":75");
        assertThat(body).contains("\"teamId\":\"bos\"");
        assertThat(body).contains("\"name\":\"Red Sox\"");
    }

    @Test
    void getTeam_fullFlow_200() {
        Team yankees = buildTeam("nyy", "Yankees", "NYY", "AL East", "AL", 50, 30);
        when(teamRepository.findById("nyy")).thenReturn(Optional.of(yankees));

        ResponseEntity<String> response = restTemplate.getForEntity("/api/v1/teams/nyy", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        String body = response.getBody();
        assertThat(body).contains("\"teamId\":\"nyy\"");
        assertThat(body).contains("\"name\":\"Yankees\"");
        assertThat(body).contains("\"abbreviation\":\"NYY\"");
        assertThat(body).contains("\"division\":\"AL East\"");
        assertThat(body).contains("\"league\":\"AL\"");
        assertThat(body).contains("\"wins\":50");
        assertThat(body).contains("\"losses\":30");
        assertThat(body).contains("\"winPct\":0.625");
        assertThat(body).contains("\"runDifferential\":75");
    }

    @Test
    void getTeam_notFound_500() {
        // Verifies current behavior: TeamService throws generic RuntimeException,
        // GlobalExceptionHandler maps to 500 ProblemDetail.
        // Will change to 404 when dedicated TeamNotFoundException is introduced.
        when(teamRepository.findById("unknown")).thenReturn(Optional.empty());

        ResponseEntity<String> response = restTemplate.getForEntity("/api/v1/teams/unknown", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).contains("Internal Server Error");
        assertThat(response.getBody()).contains("Team not found: unknown");
    }

    @Test
    void getRoster_fullFlow_200() {
        Player judge = Player.builder()
                .playerId("p1").name("Aaron Judge").position("Outfield")
                .positionAbbrev("OF").teamId("nyy").active(true).jerseyNumber("99")
                .build();
        Player soto = Player.builder()
                .playerId("p2").name("Juan Soto").position("Outfield")
                .positionAbbrev("OF").teamId("nyy").active(true).jerseyNumber("22")
                .build();
        when(playerRepository.findByTeamId("nyy")).thenReturn(List.of(judge, soto));

        ResponseEntity<String> response = restTemplate.getForEntity("/api/v1/teams/nyy/roster", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        String body = response.getBody();
        assertThat(body).contains("\"playerId\":\"p1\"");
        assertThat(body).contains("\"name\":\"Aaron Judge\"");
        assertThat(body).contains("\"playerId\":\"p2\"");
        assertThat(body).contains("\"name\":\"Juan Soto\"");
    }

    @Test
    void compareTeams_fullFlow_200() {
        Team yankees = buildTeam("nyy", "Yankees", "NYY", "AL East", "AL", 50, 30);
        Team redSox = buildTeam("bos", "Red Sox", "BOS", "AL East", "AL", 45, 35);
        when(teamRepository.findById("nyy")).thenReturn(Optional.of(yankees));
        when(teamRepository.findById("bos")).thenReturn(Optional.of(redSox));

        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/teams/compare?teamIds=nyy,bos", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        String body = response.getBody();
        assertThat(body).contains("\"teams\"");
        assertThat(body).contains("\"metrics\"");
        assertThat(body).contains("\"metricName\":\"wins\"");
        assertThat(body).contains("\"metricName\":\"losses\"");
        assertThat(body).contains("\"metricName\":\"winPct\"");
        assertThat(body).contains("\"metricName\":\"runDifferential\"");
    }

    private Team buildTeam(String id, String name, String abbrev, String division,
                           String league, int wins, int losses) {
        int runsScored = 400;
        int runsAllowed = 325;
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
                .runsScored(runsScored)
                .runsAllowed(runsAllowed)
                .runDifferential(runsScored - runsAllowed)
                .build();
    }
}
