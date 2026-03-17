package com.buzzball.integration;

import com.azure.spring.cloud.autoconfigure.implementation.cosmos.AzureCosmosAutoConfiguration;
import com.azure.spring.cloud.autoconfigure.implementation.data.cosmos.CosmosDataAutoConfiguration;
import com.azure.spring.cloud.autoconfigure.implementation.data.cosmos.CosmosRepositoriesAutoConfiguration;
import com.buzzball.model.MatchupProjection;
import com.buzzball.repository.MatchupProjectionRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@Disabled("TODO fix application context errors — WebTestClient requires spring-boot-starter-webflux")
@SpringBootTest()
@EnableAutoConfiguration(exclude = {
        AzureCosmosAutoConfiguration.class,
        CosmosDataAutoConfiguration.class,
        CosmosRepositoriesAutoConfiguration.class
})
public class MatchupIntegrationTest {

    @MockBean
    private MatchupProjectionRepository matchupProjectionRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void getMatchups_200() {

    }
}
