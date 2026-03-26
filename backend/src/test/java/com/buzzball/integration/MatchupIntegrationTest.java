package com.buzzball.integration;

import com.buzzball.model.MatchupProjection;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;


public class MatchupIntegrationTest extends BaseIntegrationTest {

    @Test
    void getMatchups_200() {
        when(matchupProjectionRepository.findByPlayerId(anyString()))
                .thenReturn(stubMatchups());

        ResponseEntity<List<MatchupProjection>> actual = restTemplate.exchange("/api/v1/matchups/1234/upcoming",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<MatchupProjection>>() {
                }
        );

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
    }


    @Test
    void addProjection_200() {
        MatchupProjection expectedMatchup = stubMatchups().getFirst();

        when(matchupProjectionRepository.findById(anyString()))
                .thenReturn(Optional.of(expectedMatchup));

        ResponseEntity<MatchupProjection> actual = restTemplate.exchange(
                "/api/v1/matchups/1234/project?pitcherId=56&gameDate=03302026",
                HttpMethod.POST,
                null,
                MatchupProjection.class
        );

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(actual.getBody()).isEqualTo(expectedMatchup);
    }

    private static List<MatchupProjection> stubMatchups() {
        MatchupProjection e1 = new MatchupProjection();
        MatchupProjection e2 = new MatchupProjection();
        MatchupProjection e3 = new MatchupProjection();
        e1.setGameDate("2026-03-30");
        e1.setId("1");
        e1.setPlayerId("1234");
        e2.setPlayerId("1234");
        e2.setId("2");
        e3.setId("3");
        e3.setPlayerId("1234");
        e2.setGameDate("2026-04-01");
        e3.setGameDate("2026-03-31");

        return List.of(e1, e2, e3);
    }
}
