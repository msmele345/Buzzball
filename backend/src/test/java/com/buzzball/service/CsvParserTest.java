package com.buzzball.service;

import com.buzzball.model.StatcastRow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class CsvParserTest {


    private CsvParser csvParser;

    @BeforeEach
    void setUp() {
        csvParser = new CsvParser();
    }

    @Test
    void parseCsv() {
        String csvData = "\"last_name, first_name\",\"player_id\",\"year\",\"pa\",\"bip\",\"ba\",\"est_ba\",\"est_ba_minus_ba_diff\",\"slg\",\"est_slg\",\"est_slg_minus_slg_diff\",\"woba\",\"est_woba\",\"est_woba_minus_woba_diff\"\n" +
                "\"Judge, Aaron\",\"592450\",\"2025\",\"679\",\"388\",0.331,0.318,0.013,0.688,0.672,0.016,0.463,0.451,0.012\n" +
                "\"Ohtani, Shohei\",\"660271\",\"2025\",\"727\",\"426\",0.282,0.274,0.008,0.622,0.649,-0.027,0.418,0.425,-0.007\n" +
                "\"Devers, Rafael\",\"646240\",\"2025\",\"729\",\"419\",0.252,0.244,0.008,0.479,0.487,-0.008,0.365,0.367,-0.002\n";

        List<StatcastRow> actual = csvParser.parseCsv(csvData);

        StatcastRow row1 = new StatcastRow();
        row1.setPlayerId("646240");
        assertThat(actual.getLast().getPlayerId()).isEqualTo(row1.getPlayerId());
        assertThat(actual.size()).isEqualTo(3);

    }
}