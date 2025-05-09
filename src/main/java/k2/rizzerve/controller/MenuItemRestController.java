// src/main/java/k2/rizzerve/controller/MenuItemRestController.java
package k2.rizzerve.controller;

import k2.rizzerve.model.Category;
import k2.rizzerve.model.MenuItem;
import k2.rizzerve.service.CategoryService;
import k2.rizzerve.service.MenuItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menuitems")
public class MenuItemRestController {
    private final MenuItemService svc;
    private final CategoryService catSvc;

    public MenuItemRestController(MenuItemService svc, CategoryService catSvc) {
        this.svc = svc;
        this.catSvc = catSvc;
    }

    @GetMapping
    public List<MenuItem> listAll() {
        return svc.listAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MenuItem> getOne(@PathVariable Long id) {
        MenuItem item = svc.getById(id);
        return (item != null)
                ? ResponseEntity.ok(item)
                : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<MenuItem> create(@RequestBody MenuItem menuItem) {
        // re‐attach the real Category
        Category cat = catSvc.getById(menuItem.getCategory().getId());
        menuItem.setCategory(cat);
        MenuItem saved = svc.add(menuItem);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MenuItem> update(
            @PathVariable Long id,
            @RequestBody MenuItem menuItem
    ) {
        // force ID and re-attach Category
        menuItem.setId(id);
        Category cat = catSvc.getById(menuItem.getCategory().getId());
        menuItem.setCategory(cat);
        MenuItem updated = svc.update(menuItem);
        return (updated != null)
                ? ResponseEntity.ok(updated)
                : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean ok = svc.delete(id);
        return ok
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
