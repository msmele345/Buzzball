package com.buzzball.model;

import com.azure.spring.data.cosmos.core.mapping.Container;
import com.azure.spring.data.cosmos.core.mapping.PartitionKey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Container(containerName = "pitching-stats")
public class PitchingStats {
    @Id
    private String id; // playerId-season
    @PartitionKey
    private String playerId;
    private int season;
    // Standard stats
    private int gamesPlayed;
    private int gamesStarted;
    private double inningsPitched;
    private double era;
    private int wins;
    private int losses;
    private int saves;
    private int strikeouts;
    private int walks;
    private double whip;
    private double battingAverageAgainst;
    // Statcast advanced
    private Double spinRateFastball;
    private Double xera;
    private Double whiffPct;
    private Double chasePct;
    private Double xba;
    private Double exitVelocityAgainst;
    // FanGraphs
    private Double fip;
    private Double xfip;
    private Double fWar;
    private Double babip;
    private Double lob;
}
