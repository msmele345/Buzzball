package com.buzzball.service.prediction;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Park factor service using static configuration.
 * Values represent run-scoring environment relative to league average (1.00 = neutral).
 * Source: multi-year Baseball Savant park factors.
 */
@Slf4j
@Service
public class ParkFactorService {

    // MLB venue IDs from statsapi.mlb.com → park factor (runs, multi-year average)
    private static final Map<String, Double> PARK_FACTORS = Map.ofEntries(
        Map.entry("coors-field", 1.21),           // COL
        Map.entry("great-american-ball-park", 1.10), // CIN
        Map.entry("globe-life-field", 1.07),       // TEX
        Map.entry("yankee-stadium", 1.05),          // NYY
        Map.entry("fenway-park", 1.04),             // BOS
        Map.entry("wrigley-field", 1.03),           // CHC
        Map.entry("oracle-park", 0.93),             // SF
        Map.entry("petco-park", 0.91),              // SD
        Map.entry("t-mobile-park", 0.90),           // SEA
        Map.entry("tropicana-field", 0.92)          // TB
    );

    private static final double NEUTRAL_PARK_FACTOR = 1.00;

    /**
     * Returns the park factor for a given venue name.
     * Normalizes the venue name for lookup. Returns 1.00 if unknown.
     */
    public double getParkFactor(String venueName) {
        if (venueName == null || venueName.isBlank()) {
            return NEUTRAL_PARK_FACTOR;
        }
        String key = venueName.toLowerCase().replace(" ", "-");
        double factor = PARK_FACTORS.getOrDefault(key, NEUTRAL_PARK_FACTOR);
        log.debug("Park factor for '{}': {}", venueName, factor);
        return factor;
    }
}
