package com.buzzball.mapper;

import com.buzzball.dto.*;
import com.buzzball.model.*;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PlayerMapper {
    PlayerSummaryDto toSummaryDto(Player player);
    PlayerDetailDto toDetailDto(Player player);
    BattingStatsDto toBattingStatsDto(BattingStats stats);
    PitchingStatsDto toPitchingStatsDto(PitchingStats stats);
    FieldingStatsDto toFieldingStatsDto(FieldingStats stats);
}
