package com.buzzball.repository;

import com.azure.spring.data.cosmos.repository.CosmosRepository;
import com.buzzball.model.FieldingStats;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FieldingStatsRepository extends CosmosRepository<FieldingStats, String> {
    List<FieldingStats> findByPlayerId(String playerId);
    List<FieldingStats> findByPlayerIdAndSeason(String playerId, int season);
}
