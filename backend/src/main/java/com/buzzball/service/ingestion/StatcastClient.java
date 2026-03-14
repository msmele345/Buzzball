package com.buzzball.service.ingestion;

import com.buzzball.model.StatcastRow;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Slf4j
@Service
public class StatcastClient {

    private static final String SAVANT_BASE_URL = "https://baseballsavant.mlb.com";

    private final RestClient restClient;
    private final CsvMapper csvMapper;

    public StatcastClient(RestClient.Builder restClientBuilder, CsvMapper csvMapper) {
        this.restClient = restClientBuilder.baseUrl(SAVANT_BASE_URL).build();
        this.csvMapper = csvMapper;
    }

    public List<StatcastRow> fetchBattingStatcast(int season) {
        log.info("Fetching Statcast batting data for season {}", season);
        String csvData = this.restClient.get()
                .uri("/leaderboard/expected_statistics?type=batter&year={year}&position=&team=&min=q&csv=true", season)
                .retrieve()
                .body(String.class);
        return parseCsv(csvData);
    }

    public List<StatcastRow> fetchPitchingStatcast(int season) {
        log.info("Fetching Statcast pitching data for season {}", season);
        String csvData = this.restClient.get()
                .uri("/leaderboard/expected_statistics?type=pitcher&year={year}&position=&team=&min=q&csv=true", season)
                .retrieve()
                .body(String.class);
        return parseCsv(csvData);
    }

    private List<StatcastRow> parseCsv(String csvData) {
        if (csvData == null || csvData.isBlank()) {
            log.warn("Empty CSV data received from Statcast");
            return List.of();
        }
        try {
            CsvSchema schema = CsvSchema.emptySchema().withHeader();
            MappingIterator<StatcastRow> iterator = csvMapper
                    .readerFor(StatcastRow.class)
                    .with(schema)
                    .readValues(csvData);
            List<StatcastRow> rows = iterator.readAll();
            log.info("Parsed {} Statcast rows", rows.size());
            return rows;
        } catch (Exception e) {
            log.error("Failed to parse Statcast CSV: {}", e.getMessage(), e);
            throw new RuntimeException("Statcast CSV schema may have changed — failing loudly: " + e.getMessage(), e);
        }
    }
}
