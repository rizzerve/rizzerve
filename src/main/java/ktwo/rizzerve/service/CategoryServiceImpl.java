package ktwo.rizzerve.service;

import ktwo.rizzerve.controller.MenuUpdateSSEController;
import ktwo.rizzerve.model.Category;
import ktwo.rizzerve.model.MenuItem;
import ktwo.rizzerve.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository repo;
    private final MenuUpdateSSEController sseController;
    public CategoryServiceImpl(CategoryRepository repo, MenuUpdateSSEController sseController) {
        this.repo = repo;
        this.sseController = sseController;
    }

    @Override public Category add(Category c)            {
        return repo.save(c); }
    @Override public List<Category> listAll()             { return repo.findAll(); }
    @Override public Category getById(Long id)            {
        return repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found: "+id));
    }
    @Override public Category update(Long id, Category d) {
        Category e = getById(id);
        e.setName(d.getName());
        Category updated = repo.save(e);
        sseController.notifyAllClients();
        return updated;
    }
    @Override public void delete(Long id) {
        repo.deleteById(id);
        sseController.notifyAllClients();
    }
}
