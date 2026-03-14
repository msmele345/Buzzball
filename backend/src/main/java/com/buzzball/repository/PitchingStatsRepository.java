package com.buzzball.repository;

import com.azure.spring.data.cosmos.repository.CosmosRepository;
import com.buzzball.model.PitchingStats;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PitchingStatsRepository extends CosmosRepository<PitchingStats, String> {
    List<PitchingStats> findByPlayerId(String playerId);
    Optional<PitchingStats> findByPlayerIdAndSeason(String playerId, int season);
}
