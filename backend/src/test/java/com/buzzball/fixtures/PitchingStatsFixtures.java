package com.buzzball.fixtures;

import com.buzzball.model.PitchingStats;

import java.util.List;

public class PitchingStatsFixtures {

    /** Ace / Cy Young-calibre starter — NYY (playerId = p5) */
    public static final PitchingStats ACE_2024 = PitchingStats.builder()
            .id("p5-2024")
            .playerId("p5")
            .season(2024)
            .gamesPlayed(32)
            .gamesStarted(32)
            .inningsPitched(210.1)
            .era(2.48)
            .wins(18)
            .losses(6)
            .saves(0)
            .strikeouts(237)
            .walks(48)
            .whip(0.98)
            .battingAverageAgainst(0.208)
            .spinRateFastball(2420.0)
            .xera(2.61)
            .whiffPct(32.4)
            .chasePct(34.1)
            .xba(0.212)
            .exitVelocityAgainst(86.3)
            .fip(2.72)
            .xfip(2.85)
            .fWar(7.8)
            .babip(0.278)
            .lob(0.782)
            .build();

    /** Mid-rotation starter — BOS (playerId = p6) */
    public static final PitchingStats MID_ROTATION_2024 = PitchingStats.builder()
            .id("p6-2024")
            .playerId("p6")
            .season(2024)
            .gamesPlayed(28)
            .gamesStarted(28)
            .inningsPitched(162.0)
            .era(3.87)
            .wins(10)
            .losses(11)
            .saves(0)
            .strikeouts(154)
            .walks(58)
            .whip(1.28)
            .battingAverageAgainst(0.248)
            .spinRateFastball(2210.0)
            .xera(3.95)
            .whiffPct(24.6)
            .chasePct(29.8)
            .xba(0.245)
            .exitVelocityAgainst(89.1)
            .fip(3.74)
            .xfip(3.88)
            .fWar(2.9)
            .babip(0.301)
            .lob(0.714)
            .build();

    /** Closer / elite reliever — LAD (playerId = p7) */
    public static final PitchingStats CLOSER_2024 = PitchingStats.builder()
            .id("p7-2024")
            .playerId("p7")
            .season(2024)
            .gamesPlayed(62)
            .gamesStarted(0)
            .inningsPitched(64.2)
            .era(1.95)
            .wins(4)
            .losses(2)
            .saves(38)
            .strikeouts(88)
            .walks(14)
            .whip(0.82)
            .battingAverageAgainst(0.181)
            .spinRateFastball(2510.0)
            .xera(2.10)
            .whiffPct(38.7)
            .chasePct(37.2)
            .xba(0.185)
            .exitVelocityAgainst(84.7)
            .fip(1.88)
            .xfip(2.05)
            .fWar(2.1)
            .babip(0.256)
            .lob(0.841)
            .build();

    public static List<PitchingStats> all() {
        return List.of(ACE_2024, MID_ROTATION_2024, CLOSER_2024);
    }
}

