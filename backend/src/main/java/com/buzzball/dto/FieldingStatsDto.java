package com.buzzball.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class FieldingStatsDto {
    String playerId;
    int season;
    String position;
    int games;
    int putouts;
    int assists;
    int errors;
    double fieldingPct;
    Integer oaa;
    Integer drs;
}
