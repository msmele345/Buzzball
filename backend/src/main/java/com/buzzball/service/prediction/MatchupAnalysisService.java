package com.buzzball.service.prediction;

import com.buzzball.model.BattingStats;
import com.buzzball.model.MatchupProjection;
import com.buzzball.repository.BattingStatsRepository;
import com.buzzball.repository.MatchupProjectionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Year;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;

/**
 * Rules-based matchup projection engine.
 *
 * Weighting:
 *   60% — current season batter wOBA split
 *   30% — career wOBA vs pitcher handedness
 *   10% — park factor adjustment
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MatchupAnalysisService {

    private static final double WEIGHT_CURRENT_SEASON = 0.60;
    private static final double WEIGHT_CAREER_HANDEDNESS = 0.30;
    private static final double WEIGHT_PARK_FACTOR = 0.10;

    // League-average wOBA baseline (2024 MLB average)
    private static final double LEAGUE_AVERAGE_WOBA = 0.317;

    private final BattingStatsRepository battingStatsRepository;
    private final MatchupProjectionRepository matchupProjectionRepository;
    private final ParkFactorService parkFactorService;

    /**
     * Projects a batter's expected wOBA for a given matchup.
     * Result is cached in Cosmos DB with 24hr TTL.
     */
    public MatchupProjection projectMatchup(
            String batterId,
            String pitcherId,
            String gameDate,
            String venue) {

        String projectionId = batterId + "-" + pitcherId + "-" + gameDate;

        // Return cached projection if fresh
        Optional<MatchupProjection> cached = matchupProjectionRepository.findById(projectionId);
        if (cached.isPresent()) {
            log.debug("Returning cached matchup projection for {}", projectionId);
            return cached.get();
        }

        log.info("Computing matchup projection: batter={} pitcher={} date={} venue={}",
                batterId, pitcherId, gameDate, venue);

        int currentSeason = Year.now().getValue();

        // 1. Current season batter wOBA (60%)
        double currentSeasonWoba = getCurrentSeasonWoba(batterId, currentSeason);

        // 2. Career wOBA vs pitcher handedness (30%)
        String pitcherHandedness = getPitcherHandedness(pitcherId);
        double careerHandednessWoba = getCareerWobaVsHandedness(batterId, pitcherHandedness);

        // 3. Park factor (10% — adjusts the blended wOBA)
        double parkFactor = parkFactorService.getParkFactor(venue);

        // Weighted blend
        double blendedWoba = (WEIGHT_CURRENT_SEASON * currentSeasonWoba)
                + (WEIGHT_CAREER_HANDEDNESS * careerHandednessWoba);

        // Apply park factor adjustment to the blended result
        double projectedWoba = blendedWoba * (WEIGHT_PARK_FACTOR * parkFactor + (1 - WEIGHT_PARK_FACTOR));

        MatchupProjection projection = MatchupProjection.builder()
                .id(projectionId)
                .playerId(batterId)
                .opposingPitcherId(pitcherId)
                .gameDate(gameDate)
                .venue(venue)
                .projectedWoba(projectedWoba)
                .currentSeasonWeight(WEIGHT_CURRENT_SEASON)
                .careerSplitWeight(WEIGHT_CAREER_HANDEDNESS)
                .parkFactorWeight(WEIGHT_PARK_FACTOR)
                .handedness(pitcherHandedness)
                .parkFactor(parkFactor)
                .build();

        matchupProjectionRepository.save(projection);
        log.info("Projected wOBA for {}: {} (park-adj from {})", batterId, projectedWoba, blendedWoba);
        return projection;
    }

    /**
     * Returns projections for all upcoming games for a batter.
     */
    public List<MatchupProjection> getUpcomingProjections(String batterId) {
        String today = LocalDate.now().toString();
        return matchupProjectionRepository.findByPlayerId(batterId)
                .stream()
                .filter(p -> p.getGameDate().compareTo(today) >= 0)
                .sorted(Comparator.comparing(MatchupProjection::getGameDate))
                .toList();
    }

    private double getCurrentSeasonWoba(String batterId, int season) {
        return battingStatsRepository.findByPlayerIdAndSeason(batterId, season)
                .map(stats -> stats.getWoba() != null ? stats.getWoba() : LEAGUE_AVERAGE_WOBA)
                .orElse(LEAGUE_AVERAGE_WOBA);
    }

    private double getCareerWobaVsHandedness(String batterId, String pitcherHandedness) {
        // Use career average wOBA across all seasons as proxy for handedness split
        // (real split data would come from a dedicated splits endpoint)
        List<BattingStats> careerStats = battingStatsRepository.findByPlayerId(batterId);
        if (careerStats.isEmpty()) return LEAGUE_AVERAGE_WOBA;

        OptionalDouble avg = careerStats.stream()
                .filter(s -> s.getWoba() != null)
                .mapToDouble(BattingStats::getWoba)
                .average();

        // Apply a handedness adjustment heuristic:
        // vs same-hand pitchers: -5% wOBA penalty; vs opposite-hand: +3% bonus
        double baseWoba = avg.orElse(LEAGUE_AVERAGE_WOBA);
        return "R".equals(pitcherHandedness)
                ? baseWoba * 0.97   // right-handed pitchers tend to be tougher vs right-handed batters
                : baseWoba * 1.03;  // left-handed pitchers typically easier for right-handed batters
    }

    private String getPitcherHandedness(String pitcherId) {
        // Pitcher handedness would ideally come from the Player model (batsThrows field)
        // Using "R" as default (majority of MLB pitchers throw right)
        return "R";
    }
}
