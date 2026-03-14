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
@Container(containerName = "teams")
public class Team {
    @Id
    private String teamId;
    @PartitionKey
    private String division;
    private String name;
    private String abbreviation;
    private String league;
    private String venue;
    private int wins;
    private int losses;
    private double winPct;
    private double gamesBack;
    private int runsScored;
    private int runsAllowed;
    private int runDifferential;
}
