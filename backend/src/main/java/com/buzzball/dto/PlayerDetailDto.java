package com.buzzball.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PlayerDetailDto {
    String playerId;
    String name;
    String position;
    String positionAbbrev;
    String teamId;
    boolean active;
    String jerseyNumber;
    String birthDate;
    String batsThrows;
}
