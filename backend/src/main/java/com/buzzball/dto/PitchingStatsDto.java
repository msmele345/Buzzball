package com.buzzball.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PitchingStatsDto {
    String playerId;
    int season;
    int gamesPlayed;
    int gamesStarted;
    double inningsPitched;
    double era;
    int wins;
    int losses;
    int saves;
    int strikeouts;
    int walks;
    double whip;
    // Statcast
    Double spinRateFastball;
    Double xera;
    Double whiffPct;
    Double chasePct;
    // FanGraphs
    Double fip;
    Double xfip;
    Double fWar;
    Double babip;
}
