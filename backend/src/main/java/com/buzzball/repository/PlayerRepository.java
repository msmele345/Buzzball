package com.buzzball.repository;

import com.azure.spring.data.cosmos.repository.CosmosRepository;
import com.buzzball.model.Player;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PlayerRepository extends CosmosRepository<Player, String> {
    List<Player> findByTeamId(String teamId);
    List<Player> findByActiveTrue();
}
