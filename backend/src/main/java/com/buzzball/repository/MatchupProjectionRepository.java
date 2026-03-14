package com.buzzball.repository;

import com.azure.spring.data.cosmos.repository.CosmosRepository;
import com.buzzball.model.MatchupProjection;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MatchupProjectionRepository extends CosmosRepository<MatchupProjection, String> {
    List<MatchupProjection> findByPlayerId(String playerId);
    List<MatchupProjection> findByPlayerIdAndGameDate(String playerId, String gameDate);
}
