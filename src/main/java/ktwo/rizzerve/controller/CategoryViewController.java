package ktwo.rizzerve.controller;

import ktwo.rizzerve.model.Category;
import ktwo.rizzerve.service.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/categories")
public class CategoryViewController {

    private final CategoryService service;

    public CategoryViewController(CategoryService service) {
        this.service = service;
    }

    @GetMapping
    public String listAll(Model model) {
        model.addAttribute("categories", service.listAll());
        return "category_list";      // renders category_list.html
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("category", new Category());
        return "category_form";      // renders category_form.html
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes attrs) {
        try {
            model.addAttribute("category", service.getById(id));
            return "category_form";
        } catch (IllegalArgumentException e) {
            attrs.addFlashAttribute("error", e.getMessage());
            return "redirect:/categories";
        }
    }

    @PostMapping
    public String save(@ModelAttribute Category category, RedirectAttributes attrs) {
        service.add(category);
        attrs.addFlashAttribute("success", "Saved!");
        return "redirect:/categories";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes attrs) {
        service.delete(id);
        attrs.addFlashAttribute("success", "Deleted!");
        return "redirect:/categories";
    }
}
