package ktwo.rizzerve.controller;

import ktwo.rizzerve.model.Category;
import ktwo.rizzerve.model.MenuItem;
import ktwo.rizzerve.service.CategoryService;
import ktwo.rizzerve.service.MenuItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class MenuItemViewControllerTest {

    @Mock
    private MenuItemService menuService;

    @Mock
    private CategoryService categoryService;

    @Mock
    private Model model;

    @Mock
    private RedirectAttributes attrs;

    @InjectMocks
    private MenuItemViewController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void populateCategories_shouldReturnCategories() {
        List<Category> categories = List.of(new Category.Builder().name("Food").build());
        when(categoryService.listAll()).thenReturn(categories);

        List<Category> result = controller.populateCategories();

        assertThat(result).isEqualTo(categories);
    }

    @Test
    void listAll_shouldAddMenusAndReturnView() {
        List<MenuItem> menus = List.of(new MenuItem.Builder().name("Nasi").price(BigDecimal.ONE).build());
        when(menuService.listAll()).thenReturn(menus);

        String view = controller.listAll(model);

        verify(model).addAttribute("menus", menus);
        assertThat(view).isEqualTo("menu_list");
    }

    @Test
    void showCreateForm_shouldAddEmptyMenuAndReturnView() {
        String view = controller.showCreateForm(model);

        verify(model).addAttribute(eq("menu"), any(MenuItem.class));
        assertThat(view).isEqualTo("menu_form");
    }

    @Test
    void showEditForm_whenFound_shouldAddMenuAndReturnView() {
        MenuItem item = new MenuItem.Builder().name("Bakso").price(BigDecimal.TEN).build();
        when(menuService.getById(1L)).thenReturn(item);

        String view = controller.showEditForm(1L, model, attrs);

        verify(model).addAttribute("menu", item);
        assertThat(view).isEqualTo("menu_form");
    }

    @Test
    void showEditForm_whenNotFound_shouldRedirectWithError() {
        when(menuService.getById(999L)).thenThrow(new IllegalArgumentException("Not found"));

        String view = controller.showEditForm(999L, model, attrs);

        verify(attrs).addFlashAttribute(eq("error"), contains("Not found"));
        assertThat(view).isEqualTo("redirect:/menus");
    }

    @Test
    void save_shouldAddMenuAndRedirect() {
        MenuItem newItem = new MenuItem.Builder().name("Soto").price(BigDecimal.ONE).build();

        String view = controller.save(newItem, attrs);

        verify(menuService).add(newItem);
        verify(attrs).addFlashAttribute("success", "Saved!");
        assertThat(view).isEqualTo("redirect:/menus");
    }

    @Test
    void delete_shouldDeleteMenuAndRedirect() {
        String view = controller.delete(7L, attrs);

        verify(menuService).delete(7L);
        verify(attrs).addFlashAttribute("success", "Deleted!");
        assertThat(view).isEqualTo("redirect:/menus");
    }
}
