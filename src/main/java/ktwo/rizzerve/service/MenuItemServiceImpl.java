package ktwo.rizzerve.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import ktwo.rizzerve.controller.MenuUpdateSSEController;
import ktwo.rizzerve.model.MenuItem;
import ktwo.rizzerve.repository.MenuItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MenuItemServiceImpl implements MenuItemService {
    private final MenuItemRepository repo;
    private final MenuUpdateSSEController sseController;

    // metrics
    private final Counter createCounter;
    private final Counter deleteCounter;
    private final Timer listTimer;
    private final Timer getTimer;
    private final Timer updateTimer;

    public MenuItemServiceImpl(MenuItemRepository repo,
                               MenuUpdateSSEController sseController,
                               MeterRegistry registry) {
        this.repo = repo;
        this.sseController = sseController;
        this.createCounter = registry.counter("menuitem.created");
        this.deleteCounter = registry.counter("menuitem.deleted");
        this.listTimer     = registry.timer("menuitem.list.duration");
        this.getTimer      = registry.timer("menuitem.get.duration");
        this.updateTimer   = registry.timer("menuitem.update.duration");
    }

    @Override
    public List<MenuItem> listAll() {
        MeterRegistry registry = new SimpleMeterRegistry();
        Timer.Sample sample = Timer.start(registry);
        List<MenuItem> items = repo.findAll();
        sample.stop(listTimer);
        return items;
    }


    @Override
    public MenuItem getById(Long id) {
        return getTimer.record(() ->
                repo.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("MenuItem not found: " + id))
        );
    }

    @Override
    public MenuItem add(MenuItem item) {
        createCounter.increment();
        MenuItem saved = repo.save(item);
        sseController.notifyAllClients();
        return saved;
    }

    @Override
    public MenuItem update(Long id, MenuItem data) {
        return updateTimer.record(() -> {
            MenuItem existing = getById(id);
            existing.setName(data.getName());
            existing.setPrice(data.getPrice());
            existing.setDescription(data.getDescription());
            existing.setAvailable(data.isAvailable());
            MenuItem updated = repo.save(existing);
            sseController.notifyAllClients();
            return updated;
        });
    }

    @Override
    public void delete(Long id) {
        deleteCounter.increment();
        repo.deleteById(id);
        sseController.notifyAllClients();
    }
}
