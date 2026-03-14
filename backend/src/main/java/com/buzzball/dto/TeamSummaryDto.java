package com.buzzball.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class TeamSummaryDto {
    String teamId;
    String name;
    String abbreviation;
    String division;
    String league;
    int wins;
    int losses;
    double winPct;
    double gamesBack;
    int runDifferential;
}
