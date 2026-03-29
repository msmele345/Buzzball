package com.buzzball.fixtures;

import com.buzzball.model.BattingStats;

import java.util.List;

public class BattingStatsFixtures {

    /** Elite power hitter — tied to YANKEES roster */
    public static final BattingStats JUDGE_2024 = BattingStats.builder()
            .id("p1-2024")
            .playerId("p1")
            .season(2024)
            .gamesPlayed(155)
            .atBats(530)
            .hits(162)
            .homeRuns(48)
            .rbi(120)
            .walks(90)
            .strikeouts(148)
            .stolenBases(4)
            .battingAverage(0.306)
            .onBasePercentage(.406)
            .sluggingPercentage(.629)
            .ops(1.035)
            .xba(0.298)
            .xslg(0.614)
            .xwoba(0.420)
            .exitVelocityAvg(95.8)
            .barrelPct(20.4)
            .hardHitPct(56.2)
            .launchAngleAvg(17.3)
            .sprintSpeedFt(26.1)
            .woba(0.418)
            .wrcPlus(178.0)
            .fWar(7.2)
            .babip(0.298)
            .build();

    /** High-contact, low-power hitter — tied to RED_SOX roster */
    public static final BattingStats DEVERS_2024 = BattingStats.builder()
            .id("p2-2024")
            .playerId("p2")
            .season(2024)
            .gamesPlayed(148)
            .atBats(560)
            .hits(174)
            .homeRuns(22)
            .rbi(94)
            .walks(52)
            .strikeouts(110)
            .stolenBases(2)
            .battingAverage(0.311)
            .onBasePercentage(0.372)
            .sluggingPercentage(0.498)
            .ops(0.870)
            .xba(0.303)
            .xslg(0.482)
            .xwoba(0.368)
            .exitVelocityAvg(91.4)
            .barrelPct(10.8)
            .hardHitPct(47.5)
            .launchAngleAvg(12.1)
            .sprintSpeedFt(26.8)
            .woba(0.370)
            .wrcPlus(138.0)
            .fWar(4.1)
            .babip(0.341)
            .build();

    /** Below-average bat — tied to DODGERS bench */
    public static final BattingStats UTILITY_2024 = BattingStats.builder()
            .id("p3-2024")
            .playerId("p3")
            .season(2024)
            .gamesPlayed(98)
            .atBats(280)
            .hits(68)
            .homeRuns(7)
            .rbi(32)
            .walks(24)
            .strikeouts(88)
            .stolenBases(5)
            .battingAverage(0.243)
            .onBasePercentage(0.305)
            .sluggingPercentage(0.371)
            .ops(0.676)
            .xba(0.239)
            .xslg(0.360)
            .xwoba(0.295)
            .exitVelocityAvg(86.2)
            .barrelPct(5.1)
            .hardHitPct(34.8)
            .launchAngleAvg(9.7)
            .sprintSpeedFt(28.3)
            .woba(0.301)
            .wrcPlus(88.0)
            .fWar(0.9)
            .babip(0.312)
            .build();

    public static List<BattingStats> all() {
        return List.of(JUDGE_2024, DEVERS_2024, UTILITY_2024);
    }
}

