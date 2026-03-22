package com.buzzball.integration;

import com.azure.spring.cloud.autoconfigure.implementation.cosmos.AzureCosmosAutoConfiguration;
import com.azure.spring.cloud.autoconfigure.implementation.data.cosmos.CosmosDataAutoConfiguration;
import com.azure.spring.cloud.autoconfigure.implementation.data.cosmos.CosmosRepositoriesAutoConfiguration;
import com.buzzball.integration.config.TestConfig;
import com.buzzball.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration(exclude = {
        AzureCosmosAutoConfiguration.class,
        CosmosDataAutoConfiguration.class,
        CosmosRepositoriesAutoConfiguration.class
})
@Import(TestConfig.class)
public abstract class BaseIntegrationTest {

    @Autowired
    protected TestRestTemplate restTemplate;

    // All CosmosRepository beans must be mocked to prevent Cosmos auto-config failures
    @MockBean
    protected TeamRepository teamRepository;
    @MockBean
    protected PlayerRepository playerRepository;
    @MockBean
    protected BattingStatsRepository battingStatsRepository;
    @MockBean
    protected PitchingStatsRepository pitchingStatsRepository;
    @MockBean
    protected FieldingStatsRepository fieldingStatsRepository;
    @MockBean
    protected MatchupProjectionRepository matchupProjectionRepository;
}
