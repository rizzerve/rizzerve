package k2.rizzerve.controller;

import k2.rizzerve.model.Category;
import k2.rizzerve.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryRestController {
    private final CategoryService service;

    public CategoryRestController(CategoryService service) {
        this.service = service;
    }

    /**
     * GET /api/categories
     * List all categories
     */
    @GetMapping
    public ResponseEntity<List<Category>> getAll() {
        List<Category> categories = service.listAll();
        return ResponseEntity.ok(categories);
    }

    /**
     * GET /api/categories/{id}
     * Retrieve a category by id
     */
    @GetMapping("/{id}")
    public ResponseEntity<Category> getById(@PathVariable Long id) {
        Category category = service.getById(id);
        if (category != null) {
            return ResponseEntity.ok(category);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * POST /api/categories
     * Create a new category
     */
    @PostMapping
    public ResponseEntity<Category> create(@RequestBody Category category) {
        Category saved = service.add(category);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /**
     * PUT /api/categories/{id}
     * Update an existing category
     */
    @PutMapping("/{id}")
    public ResponseEntity<Category> update(@PathVariable Long id, @RequestBody Category category) {
        category.setId(id); // ensure ID is set
        Category updated = service.update(category);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * DELETE /api/categories/{id}
     * Delete a category by id
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean deleted = service.delete(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
