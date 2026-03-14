package com.buzzball.repository;

import com.azure.spring.data.cosmos.repository.CosmosRepository;
import com.buzzball.model.BattingStats;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BattingStatsRepository extends CosmosRepository<BattingStats, String> {
    List<BattingStats> findByPlayerId(String playerId);
    Optional<BattingStats> findByPlayerIdAndSeason(String playerId, int season);
}
