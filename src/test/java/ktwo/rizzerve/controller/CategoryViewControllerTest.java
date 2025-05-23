package ktwo.rizzerve.controller;

import ktwo.rizzerve.model.Category;
import ktwo.rizzerve.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CategoryViewControllerTest {

    @Mock
    private CategoryService service;

    @Mock
    private Model model;

    @Mock
    private RedirectAttributes attrs;

    @InjectMocks
    private CategoryViewController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void listAll_shouldAddCategoriesAndReturnListView() {
        List<Category> categories = List.of(new Category.Builder().name("Minuman").build());
        when(service.listAll()).thenReturn(categories);

        String view = controller.listAll(model);

        verify(model).addAttribute("categories", categories);
        assertThat(view).isEqualTo("category_list");
    }

    @Test
    void showCreateForm_shouldReturnFormViewWithEmptyCategory() {
        String view = controller.showCreateForm(model);

        verify(model).addAttribute(eq("category"), any(Category.class));
        assertThat(view).isEqualTo("category_form");
    }

    @Test
    void showEditForm_whenFound_shouldReturnFormWithCategory() {
        Category cat = new Category.Builder().name("Makanan").build();
        when(service.getById(5L)).thenReturn(cat);

        String view = controller.showEditForm(5L, model, attrs);

        verify(model).addAttribute("category", cat);
        assertThat(view).isEqualTo("category_form");
    }

    @Test
    void showEditForm_whenNotFound_shouldRedirectWithError() {
        when(service.getById(5L)).thenThrow(new IllegalArgumentException("Not found"));

        String view = controller.showEditForm(5L, model, attrs);

        verify(attrs).addFlashAttribute("error", "Not found");
        assertThat(view).isEqualTo("redirect:/categories");
    }

    @Test
    void save_shouldAddCategoryAndRedirect() {
        Category newCat = new Category.Builder().name("Snack").build();

        String view = controller.save(newCat, attrs);

        verify(service).add(newCat);
        verify(attrs).addFlashAttribute("success", "Saved!");
        assertThat(view).isEqualTo("redirect:/categories");
    }

    @Test
    void delete_shouldRemoveCategoryAndRedirect() {
        String view = controller.delete(99L, attrs);

        verify(service).delete(99L);
        verify(attrs).addFlashAttribute("success", "Deleted!");
        assertThat(view).isEqualTo("redirect:/categories");
    }
}
