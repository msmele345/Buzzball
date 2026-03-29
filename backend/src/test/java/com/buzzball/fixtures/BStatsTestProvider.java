package com.buzzball.fixtures;

import com.buzzball.model.BattingStats;

import java.util.List;

public enum BStatsTestProvider {
    INSTANCE;

    //JUDGE_2024
    public BattingStats elitePowerHitter() {
        return BattingStats.builder()
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
                .battingAverage(0.306)
                .onBasePercentage(0.406)
                .sluggingPercentage(0.629)
                .ops(1.035)
                .wrcPlus(178.0)
                .fWar(7.2)
                .build();
    }

    public BattingStats contactHitter() {
        return BattingStats.builder()
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
                .battingAverage(0.311)
                .onBasePercentage(0.372)
                .sluggingPercentage(0.498)
                .ops(0.870)
                .wrcPlus(138.0)
                .fWar(4.1)
                .build();
    }

    public BattingStats belowAverageBat() {
        return BattingStats.builder()
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
                .battingAverage(0.243)
                .onBasePercentage(0.305)
                .sluggingPercentage(0.371)
                .ops(0.676)
                .wrcPlus(88.0)
                .fWar(0.9)
                .build();
    }

    public List<BattingStats> all() {
        return List.of(elitePowerHitter(), contactHitter(), belowAverageBat());
    }
}
