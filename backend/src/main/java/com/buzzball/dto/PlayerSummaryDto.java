package com.buzzball.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PlayerSummaryDto {
    String playerId;
    String name;
    String position;
    String positionAbbrev;
    String teamId;
    boolean active;
    String jerseyNumber;
}
