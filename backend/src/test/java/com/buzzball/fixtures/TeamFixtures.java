package com.buzzball.fixtures;

import com.buzzball.model.Team;

import java.util.List;

public class TeamFixtures {

    public static final Team YANKEES = Team.builder()
            .teamId("nyy")
            .name("New York Yankees")
            .abbreviation("NYY")
            .league("AL")
            .division("AL East")
            .venue("Yankee Stadium")
            .wins(95)
            .losses(67)
            .winPct(0.586)
            .gamesBack(0.0)
            .runsScored(820)
            .runsAllowed(680)
            .runDifferential(140)
            .build();

    public static final Team RED_SOX = Team.builder()
            .teamId("bos")
            .name("Boston Red Sox")
            .abbreviation("BOS")
            .league("AL")
            .division("AL East")
            .venue("Fenway Park")
            .wins(78)
            .losses(84)
            .winPct(0.481)
            .gamesBack(17.0)
            .runsScored(710)
            .runsAllowed(745)
            .runDifferential(-35)
            .build();

    public static final Team DODGERS = Team.builder()
            .teamId("lad")
            .name("Los Angeles Dodgers")
            .abbreviation("LAD")
            .league("NL")
            .division("NL West")
            .venue("Dodger Stadium")
            .wins(100)
            .losses(62)
            .winPct(0.617)
            .gamesBack(0.0)
            .runsScored(860)
            .runsAllowed(640)
            .runDifferential(220)
            .build();

    public static List<Team> all() {
        return List.of(YANKEES, RED_SOX, DODGERS);
    }
}

