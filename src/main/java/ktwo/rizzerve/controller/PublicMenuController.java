package ktwo.rizzerve.controller;

import ktwo.rizzerve.model.MenuItem;
import ktwo.rizzerve.service.CategoryService;
import ktwo.rizzerve.service.MenuItemService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class PublicMenuController {

    private final MenuItemService menuItemService;
    private final CategoryService categoryService;

    public PublicMenuController(MenuItemService menuItemService, CategoryService categoryService) {
        this.menuItemService = menuItemService;
        this.categoryService = categoryService;
    }

    @GetMapping("/menu")
    public String showPublicMenu(Model model) {
        List<MenuItem> items = menuItemService.listAll();
        Map<Long, List<MenuItem>> itemsByCategory = items.stream()
                .collect(Collectors.groupingBy(item -> item.getCategory().getId()));
        model.addAttribute("categories", categoryService.listAll());
        model.addAttribute("itemsByCategory", itemsByCategory);
        return "public_menu";
    }
}