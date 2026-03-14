package com.buzzball.service;

import com.buzzball.dto.*;
import com.buzzball.mapper.TeamMapper;
import com.buzzball.model.Team;
import com.buzzball.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamMapper teamMapper;

    @Cacheable("standings")
    public List<TeamSummaryDto> getAllTeams() {
        List<Team> teams = new ArrayList<>();
        teamRepository.findAll().forEach(teams::add);
        return teams.stream().map(teamMapper::toSummaryDto).toList();
    }

    public TeamSummaryDto getTeam(String teamId) {
        return teamRepository.findById(teamId)
                .map(teamMapper::toSummaryDto)
                .orElseThrow(() -> new RuntimeException("Team not found: " + teamId));
    }

    public List<TeamSummaryDto> getTeamsByLeague(String league) {
        return teamRepository.findByLeague(league)
                .stream()
                .map(teamMapper::toSummaryDto)
                .toList();
    }

    public List<TeamSummaryDto> getTeamsByDivision(String division) {
        return teamRepository.findByDivision(division)
                .stream()
                .map(teamMapper::toSummaryDto)
                .toList();
    }

    public TeamComparisonDto compareTeams(List<String> teamIds) {
        List<TeamSummaryDto> teams = teamIds.stream()
                .map(this::getTeam)
                .toList();

        List<TeamComparisonDto.ComparisonMetric> metrics = List.of(
                buildMetric("wins", "Wins", teams, t -> (double) t.getWins()),
                buildMetric("losses", "Losses", teams, t -> (double) t.getLosses()),
                buildMetric("winPct", "Win %", teams, TeamSummaryDto::getWinPct),
                buildMetric("runDifferential", "Run Diff", teams, t -> (double) t.getRunDifferential())
        );

        return TeamComparisonDto.builder()
                .teams(teams)
                .metrics(metrics)
                .build();
    }

    private TeamComparisonDto.ComparisonMetric buildMetric(
            String name, String label,
            List<TeamSummaryDto> teams,
            java.util.function.Function<TeamSummaryDto, Double> extractor) {
        return TeamComparisonDto.ComparisonMetric.builder()
                .metricName(name)
                .label(label)
                .values(teams.stream().map(extractor).toList())
                .build();
    }
}
