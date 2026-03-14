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
@Container(containerName = "matchup-projections", timeToLive = 86400) // 24 hours in seconds
public class MatchupProjection {
    @Id
    private String id; // playerId-pitcherId-gameDate
    @PartitionKey
    private String playerId;
    private String opposingPitcherId;
    private String gameDate;
    private String venue;
    private double projectedWoba;
    private double currentSeasonWeight;   // 60%
    private double careerSplitWeight;     // 30%
    private double parkFactorWeight;      // 10%
    private String handedness;            // vs L or vs R
    private double parkFactor;
}
