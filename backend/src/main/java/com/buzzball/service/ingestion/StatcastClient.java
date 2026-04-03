package com.buzzball.service.ingestion;

import com.buzzball.model.StatcastRow;
import com.buzzball.service.CsvParser;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Slf4j
@Service
public class StatcastClient {

    private final RestClient restClient;
    private final CsvParser csvParser;
    private final CsvMapper csvMapper = new CsvMapper();

    public StatcastClient(
            RestClient.Builder restClientBuilder,
            @Value("${buzzball.clients.statcast.base-url:https://baseballsavant.mlb.com}")
            String baseUrl,
            CsvParser csvParser
    ) {
        this.csvParser = csvParser;
        this.restClient = restClientBuilder.baseUrl(baseUrl).build();
    }

    public List<StatcastRow> fetchBattingStatcast(int season) {
        log.info("Fetching Statcast batting data for season {}", season);
        String csvData = this.restClient.get()
                .uri("/leaderboard/expected_statistics?type=batter&year={year}&position=&team=&min=q&csv=true", season)
                .retrieve()
                .body(String.class);
        return csvParser.parseCsv(csvData);
    }

    public List<StatcastRow> fetchPitchingStatcast(int season) {
        log.info("Fetching Statcast pitching data for season {}", season);
        String csvData = this.restClient.get()
                .uri("/leaderboard/expected_statistics?type=pitcher&year={year}&position=&team=&min=q&csv=true", season)
                .retrieve()
                .body(String.class);
        return csvParser.parseCsv(csvData);
    }
}
