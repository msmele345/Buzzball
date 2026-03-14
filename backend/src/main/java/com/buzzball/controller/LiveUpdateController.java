package com.buzzball.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@RestController
@RequestMapping("/api/v1/live")
public class LiveUpdateController {

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    @GetMapping(value = "/updates", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter getLiveUpdates() {
        SseEmitter emitter = new SseEmitter(300_000L); // 5-minute timeout

        emitters.add(emitter);

        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> {
            emitters.remove(emitter);
            emitter.complete();
        });
        emitter.onError((ex) -> emitters.remove(emitter));

        // Send initial connected event
        try {
            emitter.send(SseEmitter.event()
                    .name("connected")
                    .data("Connected to BuzzBall live updates"));
        } catch (IOException e) {
            emitters.remove(emitter);
            emitter.completeWithError(e);
        }

        return emitter;
    }

    /**
     * Called after each scheduler refresh cycle to push invalidation signals to clients.
     * Fires every 4 hours (aligned with roster refresh schedule).
     */
    @Scheduled(fixedDelay = 4 * 60 * 60 * 1000, initialDelay = 5 * 60 * 1000)
    public void broadcastRefreshEvent() {
        String payload = "{\"type\":\"data-refresh\",\"timestamp\":\"" + Instant.now() + "\"}";
        log.info("Broadcasting SSE refresh event to {} clients", emitters.size());

        List<SseEmitter> deadEmitters = new java.util.ArrayList<>();
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("data-refresh")
                        .data(payload));
            } catch (IOException e) {
                deadEmitters.add(emitter);
            }
        }
        emitters.removeAll(deadEmitters);
    }

    /**
     * Allows manual trigger of a refresh broadcast (e.g., after ingestion completes).
     */
    public void triggerRefresh(String reason) {
        String payload = "{\"type\":\"data-refresh\",\"reason\":\"" + reason
                + "\",\"timestamp\":\"" + Instant.now() + "\"}";
        List<SseEmitter> deadEmitters = new java.util.ArrayList<>();
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().name("data-refresh").data(payload));
            } catch (IOException e) {
                deadEmitters.add(emitter);
            }
        }
        emitters.removeAll(deadEmitters);
    }
}
