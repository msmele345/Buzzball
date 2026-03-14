package com.buzzball.repository;

import com.azure.spring.data.cosmos.repository.CosmosRepository;
import com.buzzball.model.Team;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TeamRepository extends CosmosRepository<Team, String> {
    List<Team> findByDivision(String division);
    List<Team> findByLeague(String league);
}
