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
@Container(containerName = "fielding-stats")
public class FieldingStats {
    @Id
    private String id; // playerId-position-season
    @PartitionKey
    private String playerId;
    private int season;
    private String position;
    private int games;
    private int putouts;
    private int assists;
    private int errors;
    private double fieldingPct;
    // Statcast
    private Integer oaa;    // Outs Above Average
    // FanGraphs/DRS
    private Integer drs;    // Defensive Runs Saved
}
