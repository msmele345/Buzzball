package com.buzzball.service.ingestion;

import com.buzzball.service.CsvParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class StatcastClientTest {

    @Mock
    private RestClient.Builder restClientBuilder;

    @Mock
    private CsvParser csvParser;

    private StatcastClient statcastClient;

    @BeforeEach
    void setUp() {
        when(restClientBuilder.baseUrl(anyString())).thenReturn(restClientBuilder);
        statcastClient = new StatcastClient(restClientBuilder, "www.someurl.com", csvParser);
    }

    @Test
    void fetchBattingStatcast_returnsListOfStatcastRowAfterParsingCsv() {

    }
}