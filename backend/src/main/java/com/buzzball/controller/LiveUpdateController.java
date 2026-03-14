package com.buzzball.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * Server-Sent Events controller for live score/stat updates — full implementation in Phase 5.
 */
@RestController
@RequestMapping("/api/v1/live")
public class LiveUpdateController {

    @GetMapping(value = "/updates", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter getLiveUpdates() {
        // Phase 5: emits events after each scheduler refresh cycle
        SseEmitter emitter = new SseEmitter(30_000L);
        emitter.complete();
        return emitter;
    }
}
