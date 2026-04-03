package com.buzzball.service;

import com.buzzball.model.StatcastRow;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class CsvParser {

    private final CsvMapper csvMapper = new CsvMapper();

    public List<StatcastRow> parseCsv(String csvData) {
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
