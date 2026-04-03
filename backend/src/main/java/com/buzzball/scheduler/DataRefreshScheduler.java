package com.buzzball.scheduler;

import com.buzzball.service.ingestion.DataIngestionOrchestrator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Year;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataRefreshScheduler {

    private final DataIngestionOrchestrator orchestrator;

    /** Rosters and basic stats: every 4 hours */
    @Scheduled(fixedDelay = 4 * 60 * 60 * 1000, initialDelay = 60_000)
    public void refreshRostersAndStandings() {
        log.info("Scheduler: starting roster/standings refresh");
        orchestrator.refreshRostersAndStandings();
    }

    /** Standings: every 2 hours */
    @Scheduled(fixedDelay = 2 * 60 * 60 * 1000, initialDelay = 45_000)
    public void refreshStandings() {
        log.info("Scheduler: starting standings refresh");
        orchestrator.refreshStandings();
    }

    /** Statcast advanced metrics: on startup (90s delay) + daily at 6 AM ET */
    @CacheEvict(value = "dashboard-trending", allEntries = true)
    @Scheduled(cron = "0 0 6 * * *", zone = "America/New_York")
    @Scheduled(fixedDelay = Long.MAX_VALUE, initialDelay = 90_000)
    public void refreshStatcast() {
        log.info("Scheduler: starting Statcast refresh");
        try {
            orchestrator.refreshStatcastData(Year.now().getValue());
        } catch (Exception e) {
            log.error("Statcast refresh failed — possible schema change: {}", e.getMessage(), e);
        }
    }

    /** FanGraphs WAR/FIP: on startup (120s delay) + daily at 7 AM ET */
    @CacheEvict(value = "dashboard-trending", allEntries = true)
    @Scheduled(cron = "0 0 7 * * *", zone = "America/New_York")
    @Scheduled(fixedDelay = Long.MAX_VALUE, initialDelay = 120_000)
    public void refreshFanGraphs() {
        log.info("Scheduler: starting FanGraphs refresh");
        orchestrator.refreshFanGraphsData(Year.now().getValue());
    }
}
