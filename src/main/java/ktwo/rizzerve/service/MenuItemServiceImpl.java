package ktwo.rizzerve.service;

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

    public MenuItemServiceImpl(MenuItemRepository repo, MenuUpdateSSEController sseController) {
        this.repo = repo;
        this.sseController = sseController;
    }

    @Override
    public List<MenuItem> listAll() {
        return repo.findAll();
    }

    @Override
    public MenuItem getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("MenuItem not found: " + id));
    }

    @Override
    public MenuItem add(MenuItem item) {
        MenuItem saved = repo.save(item);
        sseController.notifyAllClients();
        return saved;
    }

    @Override
    public MenuItem update(Long id, MenuItem data) {
        MenuItem existing = getById(id);
        existing.setName(data.getName());
        existing.setPrice(data.getPrice());
        existing.setDescription(data.getDescription());
        existing.setAvailable(data.isAvailable());
        MenuItem updated = repo.save(existing);
        sseController.notifyAllClients();
        return updated;
    }

    @Override
    public void delete(Long id) {
        repo.deleteById(id);
        sseController.notifyAllClients();
    }

}
