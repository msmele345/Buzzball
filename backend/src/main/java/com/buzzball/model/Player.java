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
@Container(containerName = "players")
public class Player {
    @Id
    private String playerId;
    @PartitionKey
    private String teamId;
    private String fullName;
    private String position;
    private String positionAbbrev;
    private boolean active;
    private String jerseyNumber;
    private String birthDate;
    private String batsThrows;
}
