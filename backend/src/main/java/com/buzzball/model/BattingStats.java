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
@Container(containerName = "batting-stats")
public class BattingStats {
    @Id
    private String id; // playerId-season
    @PartitionKey
    private String playerId;
    private int season;
    // Standard stats
    private int gamesPlayed;
    private int atBats;
    private int hits;
    private int homeRuns;
    private int rbi;
    private int walks;
    private int strikeouts;
    private int stolenBases;
    private Double battingAverage;
    private Double onBasePercentage;
    private Double sluggingPercentage;
    private Double ops;
    // Statcast advanced
    private Double xba;         // Expected Batting Average
    private Double xslg;        // Expected Slugging
    private Double xwoba;       // Expected wOBA
    private Double exitVelocityAvg;
    private Double barrelPct;
    private Double hardHitPct;
    private Double launchAngleAvg;
    private Double sprintSpeedFt;
    // FanGraphs
    private Double woba;
    private Double wrcPlus;
    private Double fWar;
    private Double babip;
}
