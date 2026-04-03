package com.buzzball.repository;

import com.azure.spring.data.cosmos.repository.CosmosRepository;
import com.azure.spring.data.cosmos.repository.Query;
import com.buzzball.model.Team;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TeamRepository extends CosmosRepository<Team, String> {
    List<Team> findByDivision(String division);
    List<Team> findByLeague(String league);

    @Query("SELECT * FROM c WHERE c.teamId = @teamId AND c.division != null")
    List<Team> findByTeamIdAndDivisionIsNotNull(@Param("teamId") String teamId);

    @Query("SELECT * FROM c WHERE c.division != null")
    List<Team> findAllByDivisionIsNotNull();
}
