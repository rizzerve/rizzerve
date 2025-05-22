package ktwo.rizzerve.service;

import ktwo.rizzerve.model.MenuItem;
import ktwo.rizzerve.repository.MenuItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MenuItemServiceImpl implements MenuItemService {
    private final MenuItemRepository repo;

    public MenuItemServiceImpl(MenuItemRepository repo) {
        this.repo = repo;
    }

    @Override
    public MenuItem add(MenuItem item) {
        return repo.save(item);
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
    public MenuItem update(Long id, MenuItem data) {
        MenuItem existing = getById(id);
        existing.setName(data.getName());
        existing.setPrice(data.getPrice());
        existing.setDescription(data.getDescription());
        existing.setAvailable(data.isAvailable());
        return repo.save(existing);
    }

    @Override
    public void delete(Long id) {
        repo.deleteById(id);
    }
}
