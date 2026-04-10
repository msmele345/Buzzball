package com.buzzball.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TrendingPlayerDto {
    private String playerId;
    private String name;
    private String position;
    private String teamId;
    private Double currentWar;
    private Double warDelta7d;
    private Double currentWoba;
    private Double wobaDelta7d;
    private Double currentFip;
    private Double fipDelta7d;
    private String trendCategory; // "hitting", "pitching", "fielding"
}
