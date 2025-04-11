package k2.rizzerve.controller;

import k2.rizzerve.model.MenuComponent;
import k2.rizzerve.model.MenuItem;
import k2.rizzerve.dto.MenuItemDTO;
import k2.rizzerve.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/menu")
public class MenuController {

    private final MenuService menuService;

    @Autowired
    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @PostMapping("/categories")
    public ResponseEntity<MenuComponent> createCategory(@RequestParam String name) {
        return ResponseEntity.ok(menuService.createMenuCategory(name));
    }

    @PostMapping("/items")
    public ResponseEntity<MenuItem> createMenuItem(
            @RequestParam(required = false) Long categoryId,
            @RequestBody MenuItemDTO menuItemDTO
    ) {
        MenuItem createdItem = menuService.createMenuItem(categoryId, menuItemDTO);
        return ResponseEntity.ok(createdItem);
    }

    @PutMapping("/items/{id}")
    public ResponseEntity<MenuItem> updateMenuItem(
            @PathVariable Long id,
            @RequestBody MenuItemDTO menuItemDTO
    ) {
        return ResponseEntity.ok(menuService.updateMenuItem(id, menuItemDTO));
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<Void> deleteMenuItem(@PathVariable Long id) {
        menuService.deleteMenuItem(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<MenuComponent>> getFullMenu() {
        return ResponseEntity.ok(menuService.getFullMenu());
    }
}
