package com.buzzball.mapper;

import com.buzzball.dto.TeamSummaryDto;
import com.buzzball.model.Team;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TeamMapper {
    TeamSummaryDto toSummaryDto(Team team);
}
