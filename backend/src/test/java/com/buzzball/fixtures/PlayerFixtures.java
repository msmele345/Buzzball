package com.buzzball.fixtures;

import com.buzzball.model.Player;

import java.util.List;

public class PlayerFixtures {

    /** Elite slugger — NYY, aligns with BattingStatsFixtures.JUDGE_2024 (playerId = p1) */
    public static final Player JUDGE = Player.builder()
            .playerId("p1")
            .teamId("nyy")
            .name("Aaron Judge")
            .position("Outfield")
            .positionAbbrev("OF")
            .active(true)
            .jerseyNumber("99")
            .birthDate("1992-04-26")
            .batsThrows("R/R")
            .build();

    /** High-contact hitter — BOS, aligns with BattingStatsFixtures.DEVERS_2024 (playerId = p2) */
    public static final Player DEVERS = Player.builder()
            .playerId("p2")
            .teamId("bos")
            .name("Rafael Devers")
            .position("Third Base")
            .positionAbbrev("3B")
            .active(true)
            .jerseyNumber("11")
            .birthDate("1996-10-22")
            .batsThrows("L/R")
            .build();

    /** Utility / bench player — LAD, aligns with BattingStatsFixtures.UTILITY_2024 (playerId = p3) */
    public static final Player UTILITY = Player.builder()
            .playerId("p3")
            .teamId("lad")
            .name("Chris Taylor")
            .position("Shortstop")
            .positionAbbrev("SS")
            .active(true)
            .jerseyNumber("3")
            .birthDate("1990-08-29")
            .batsThrows("R/R")
            .build();

    /** Inactive player — useful for testing active/inactive filtering */
    public static final Player INACTIVE = Player.builder()
            .playerId("p4")
            .teamId("nyy")
            .name("Injured Player")
            .position("Pitcher")
            .positionAbbrev("SP")
            .active(false)
            .jerseyNumber("45")
            .birthDate("1995-03-15")
            .batsThrows("R/R")
            .build();

    /** Ace starter — NYY, aligns with PitchingStatsFixtures.ACE_2024 (playerId = p5) */
    public static final Player ACE = Player.builder()
            .playerId("p5")
            .teamId("nyy")
            .name("Gerrit Cole")
            .position("Pitcher")
            .positionAbbrev("SP")
            .active(true)
            .jerseyNumber("45")
            .birthDate("1990-09-08")
            .batsThrows("R/R")
            .build();

    /** Mid-rotation starter — BOS, aligns with PitchingStatsFixtures.MID_ROTATION_2024 (playerId = p6) */
    public static final Player MID_ROTATION = Player.builder()
            .playerId("p6")
            .teamId("bos")
            .name("Brayan Bello")
            .position("Pitcher")
            .positionAbbrev("SP")
            .active(true)
            .jerseyNumber("66")
            .birthDate("2000-05-17")
            .batsThrows("R/R")
            .build();

    /** Elite closer — LAD, aligns with PitchingStatsFixtures.CLOSER_2024 (playerId = p7) */
    public static final Player CLOSER = Player.builder()
            .playerId("p7")
            .teamId("lad")
            .name("Evan Phillips")
            .position("Pitcher")
            .positionAbbrev("RP")
            .active(true)
            .jerseyNumber("59")
            .birthDate("1994-09-11")
            .batsThrows("R/R")
            .build();

    public static List<Player> all() {
        return List.of(JUDGE, DEVERS, UTILITY, INACTIVE, ACE, MID_ROTATION, CLOSER);
    }

    public static List<Player> active() {
        return List.of(JUDGE, DEVERS, UTILITY, ACE, MID_ROTATION, CLOSER);
    }

    public static List<Player> byTeam(String teamId) {
        return all().stream()
                .filter(p -> p.getTeamId().equals(teamId))
                .toList();
    }
}

