package com.buzzball.dto;

import lombok.Builder;
import lombok.Value;
import java.util.List;

@Value
@Builder
public class TeamComparisonDto {
    List<TeamSummaryDto> teams;
    List<ComparisonMetric> metrics;

    @Value
    @Builder
    public static class ComparisonMetric {
        String metricName;
        String label;
        List<Double> values; // one per team, same order as teams list
    }
}
