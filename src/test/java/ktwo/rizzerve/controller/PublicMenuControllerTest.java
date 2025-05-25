package ktwo.rizzerve.controller;

import ktwo.rizzerve.model.Category;
import ktwo.rizzerve.model.MenuItem;
import ktwo.rizzerve.service.CategoryService;
import ktwo.rizzerve.service.MenuItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PublicMenuController.class)
@AutoConfigureMockMvc(addFilters = false)
class PublicMenuControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MenuItemService menuItemService;

    @MockBean
    private CategoryService categoryService;

    private MenuItem sampleItem;
    private Category sampleCategory;

    @BeforeEach
    void setup() {
        sampleCategory = new Category();
        sampleCategory.setId(1L);
        sampleCategory.setName("Drinks");

        sampleItem = new MenuItem();
        sampleItem.setId(1L);
        sampleItem.setName("Iced Tea");
        sampleItem.setCategory(sampleCategory);

        Mockito.when(menuItemService.listAll()).thenReturn(List.of(sampleItem));
        Mockito.when(categoryService.listAll()).thenReturn(List.of(sampleCategory));
    }

    @Test
    void testShowPublicMenu_ViewAndAttributesExist() throws Exception {
        mockMvc.perform(get("/menu"))
                .andExpect(status().isOk())
                .andExpect(view().name("public_menu"))
                .andExpect(model().attributeExists("categories"))
                .andExpect(model().attributeExists("itemsByCategory"));
    }

    @Test
    void testShowPublicMenu_ContentCheck() throws Exception {
        mockMvc.perform(get("/menu"))
                .andExpect(status().isOk())
                .andExpect(view().name("public_menu"))
                .andExpect(model().attribute("categories", hasSize(1)))
                .andExpect(model().attribute("itemsByCategory", allOf(
                        aMapWithSize(1),
                        hasEntry(equalTo(1L), contains(sampleItem))
                )));
    }

    @Test
    void testShowPublicMenu_EmptyLists() throws Exception {
        Mockito.when(menuItemService.listAll()).thenReturn(Collections.emptyList());
        Mockito.when(categoryService.listAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/menu"))
                .andExpect(status().isOk())
                .andExpect(view().name("public_menu"))
                .andExpect(model().attribute("categories", hasSize(0)))
                .andExpect(model().attribute("itemsByCategory", anEmptyMap()));
    }
}
