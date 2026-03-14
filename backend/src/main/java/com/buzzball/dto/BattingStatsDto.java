package com.buzzball.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class BattingStatsDto {
    String playerId;
    int season;
    int gamesPlayed;
    int atBats;
    int hits;
    int homeRuns;
    int rbi;
    int walks;
    int strikeouts;
    int stolenBases;
    Double battingAverage;
    Double onBasePercentage;
    Double sluggingPercentage;
    Double ops;
    // Statcast
    Double xba;
    Double xslg;
    Double xwoba;
    Double exitVelocityAvg;
    Double barrelPct;
    Double hardHitPct;
    Double launchAngleAvg;
    // FanGraphs
    Double woba;
    Double wrcPlus;
    Double fWar;
    Double babip;
}
