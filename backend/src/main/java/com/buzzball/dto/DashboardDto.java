package com.buzzball.dto;

import lombok.Builder;
import lombok.Value;
import java.util.List;

@Value
@Builder
public class DashboardDto {
    List<TrendingPlayerDto> trendingHitters;
    List<TrendingPlayerDto> trendingPitchers;
    List<TeamSummaryDto> alStandings;
    List<TeamSummaryDto> nlStandings;
    List<LeagueLeaderDto> leagueLeaders;
}
