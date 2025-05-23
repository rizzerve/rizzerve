package ktwo.rizzerve.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@RestController
@RequestMapping("/menu")
public class MenuUpdateSSEController {

    private final Set<SseEmitter> emitters = new CopyOnWriteArraySet<>();

    @GetMapping("/stream")
    public SseEmitter streamUpdates() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.add(emitter);
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        return emitter;
    }

    public void notifyAllClients() {
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("menu-update")
                        .data("updated")
                        .reconnectTime(3000));
            } catch (IOException e) {
                emitters.remove(emitter);
            }
        }
    }
}
