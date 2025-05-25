package ktwo.rizzerve.controller;

import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import static org.assertj.core.api.Assertions.assertThat;

class MenuUpdateSSEControllerTest {

    @Test
    void streamUpdates_shouldRegisterEmitter() {
        MenuUpdateSSEController controller = new MenuUpdateSSEController();
        SseEmitter emitter = controller.streamUpdates();
        assertThat(emitter).isNotNull();
        emitter.complete();
    }

    @Test
    void notifyAllClients_shouldSendAndRemoveBrokenEmitters() throws Exception {
        MenuUpdateSSEController controller = new MenuUpdateSSEController();

        SseEmitter working = new SseEmitter();
        SseEmitter broken = new SseEmitter() {
            @Override
            public void send(Object object) throws IOException {
                throw new IOException("fail");
            }
        };

        var field = MenuUpdateSSEController.class.getDeclaredField("emitters");
        field.setAccessible(true);
        CopyOnWriteArraySet<SseEmitter> set = new CopyOnWriteArraySet<>();
        set.add(working);
        set.add(broken);
        field.set(controller, set);

        controller.notifyAllClients();
        assertThat(true).isTrue();
    }

    @Test
    void notifyAllClients_shouldRemoveEmitterOnIOException() throws Exception {
        MenuUpdateSSEController controller = new MenuUpdateSSEController();

        SseEmitter faulty = new SseEmitter() {
            @Override
            public void send(SseEmitter.SseEventBuilder builder) throws IOException {
                throw new IOException("Simulated failure");
            }
        };

        controller.streamUpdates();

        var field = MenuUpdateSSEController.class.getDeclaredField("emitters");
        field.setAccessible(true);
        @SuppressWarnings("unchecked")
        Set<SseEmitter> emitters = (Set<SseEmitter>) field.get(controller);
        emitters.clear();
        emitters.add(faulty);

        controller.notifyAllClients();
        assertThat(emitters).doesNotContain(faulty);
    }
}
