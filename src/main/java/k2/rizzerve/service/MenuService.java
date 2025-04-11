package k2.rizzerve.service;

import k2.rizzerve.model.MenuCategory;
import k2.rizzerve.model.MenuComponent;
import k2.rizzerve.model.MenuItem;
import k2.rizzerve.dto.MenuItemDTO;
import k2.rizzerve.repository.MenuComponentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.List;

@Service
public class MenuService {

    private final MenuComponentRepository menuRepository;

    @Autowired
    public MenuService(MenuComponentRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    @Transactional
    public MenuCategory createMenuCategory(String categoryName) {
        MenuCategory category = new MenuCategory(categoryName);
        return menuRepository.save(category);
    }

    @Transactional
    public MenuItem createMenuItem(Long categoryId, MenuItemDTO dto) {
        MenuItem menuItem = new MenuItem(
                dto.getName(),
                dto.getPrice(),
                dto.getDescription(),
                dto.isAvailable()
        );

        if (categoryId != null) {
            MenuComponent parent = menuRepository.findById(categoryId)
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            if (parent instanceof MenuCategory) {
                ((MenuCategory) parent).add(menuItem);
                menuRepository.save(parent);
            } else {
                throw new RuntimeException("Provided parent is not a valid category");
            }
        } else {
            menuRepository.save(menuItem);
        }
        return menuItem;
    }

    @Transactional
    public MenuItem updateMenuItem(Long id, MenuItemDTO dto) {
        MenuComponent component = menuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu item not found"));
        if (!(component instanceof MenuItem)) {
            throw new RuntimeException("Component is not a menu item");
        }
        MenuItem menuItem = (MenuItem) component;
        menuItem.setName(dto.getName());
        menuItem.setPrice(dto.getPrice());
        menuItem.setDescription(dto.getDescription());
        menuItem.setAvailable(dto.isAvailable());
        return menuRepository.save(menuItem);
    }

    @Transactional
    public void deleteMenuItem(Long id) {
        MenuComponent component = menuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu item not found"));
        menuRepository.delete(component);
    }

    public List<MenuComponent> getFullMenu() {
        return menuRepository.findAll();
    }
}
