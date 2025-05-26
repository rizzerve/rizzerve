package ktwo.rizzerve.controller;

import ktwo.rizzerve.model.Category;
import ktwo.rizzerve.model.MenuItem;
import ktwo.rizzerve.service.CategoryService;
import ktwo.rizzerve.service.MenuItemService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/menus")
public class MenuItemViewController {

    private static final String REDIRECT_MENUS = "redirect:/menus";

    private final MenuItemService menuService;
    private final CategoryService categoryService;

    public MenuItemViewController(
            MenuItemService menuService,
            CategoryService categoryService
    ) {
        this.menuService = menuService;
        this.categoryService = categoryService;
    }

    /** Populate the categories for the dropdown in the menu form */
    @ModelAttribute("categories")
    public List<Category> populateCategories() {
        return categoryService.listAll();
    }

    /** List all menu items */
    @GetMapping
    public String listAll(Model model) {
        model.addAttribute("menus", menuService.listAll());
        return "menu_list";
    }

    /** Show the empty form for creating a new menu item */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("menu", new MenuItem());
        return "menu_form";
    }

    /** Show the populated form for editing an existing menu item */
    @GetMapping("/{id}/edit")
    public String showEditForm(
            @PathVariable Long id,
            Model model,
            RedirectAttributes attrs
    ) {
        try {
            model.addAttribute("menu", menuService.getById(id));
            return "menu_form";
        } catch (IllegalArgumentException e) {
            attrs.addFlashAttribute("error", e.getMessage());
            return REDIRECT_MENUS;
        }
    }

    /** Handle both create & update */
    @PostMapping
    public String save(
            @ModelAttribute MenuItem menu,
            RedirectAttributes attrs
    ) {
        menuService.add(menu);
        attrs.addFlashAttribute("success", "Saved!");
        return REDIRECT_MENUS;
    }

    /** Delete a menu item */
    @PostMapping("/{id}/delete")
    public String delete(
            @PathVariable Long id,
            RedirectAttributes attrs
    ) {
        menuService.delete(id);
        attrs.addFlashAttribute("success", "Deleted!");
        return REDIRECT_MENUS;
    }
}
