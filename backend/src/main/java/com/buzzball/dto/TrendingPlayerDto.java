package com.buzzball.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class TrendingPlayerDto {
    String playerId;
    String name;
    String position;
    String teamId;
    Double currentWar;
    Double warDelta7d;
    Double currentWoba;
    Double wobaDelta7d;
    Double currentFip;
    Double fipDelta7d;
    String trendCategory; // "hitting", "pitching", "fielding"
}
