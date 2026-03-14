package com.buzzball.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class LeagueLeaderDto {
    String category;    // "wOBA", "WAR", "FIP", "HR"
    String playerId;
    String playerName;
    String teamId;
    double value;
    int rank;
}
