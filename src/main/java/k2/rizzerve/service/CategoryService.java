package k2.rizzerve.service;

import k2.rizzerve.model.Category;
import java.util.List;

public interface CategoryService {
    Category add(Category c);
    List<Category> listAll();
    Category getById(Long id);
    Category update(Long id, Category data);
    void delete(Long id);
}
