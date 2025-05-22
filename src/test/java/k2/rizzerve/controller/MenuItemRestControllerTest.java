package k2.rizzerve.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import k2.rizzerve.model.Category;
import k2.rizzerve.model.MenuItem;
import k2.rizzerve.service.CategoryService;
import k2.rizzerve.service.MenuItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class MenuItemRestControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MenuItemService menuItemService;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private MenuItemRestController menuItemController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(menuItemController)
                .build();
    }

    @Test
    public void testListAll() throws Exception {
        Category cat = new Category.Builder().name("C").build(); cat.setId(2L);
        MenuItem m = MenuItem.builder()
                .name("Cake")
                .price(new BigDecimal("4.50"))
                .description("Choco")
                .available(true)
                .category(cat)
                .build();
        m.setId(10L);

        given(menuItemService.listAll()).willReturn(List.of(m));

        mockMvc.perform(get("/api/menuitems"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].name").value("Cake"))
                .andExpect(jsonPath("$[0].category.id").value(2));
    }

    @Test
    public void testGetOne_Success() throws Exception {
        Category cat = new Category.Builder().name("C").build(); cat.setId(3L);
        MenuItem m = MenuItem.builder()
                .name("Juice")
                .price(new BigDecimal("2.00"))
                .description("Orange")
                .available(false)
                .category(cat)
                .build();
        m.setId(6L);

        given(menuItemService.getById(6L)).willReturn(m);

        mockMvc.perform(get("/api/menuitems/6"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(6))
                .andExpect(jsonPath("$.available").value(false));
    }

    @Test
    public void testGetOne_NotFound() throws Exception {
        given(menuItemService.getById(7L)).willReturn(null);

        mockMvc.perform(get("/api/menuitems/7"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreate_Success() throws Exception {
        Category cat = new Category.Builder().name("Drinks").build(); cat.setId(4L);

        MenuItem in = MenuItem.builder()
                .name("Juice")
                .price(new BigDecimal("2.00"))
                .description("Orange")
                .available(false)
                .category(cat)
                .build();

        MenuItem saved = MenuItem.builder()
                .name("Juice")
                .price(new BigDecimal("2.00"))
                .description("Orange")
                .available(false)
                .category(cat)
                .build();
        saved.setId(11L);

        given(categoryService.getById(4L)).willReturn(cat);
        given(menuItemService.add(any(MenuItem.class))).willReturn(saved);

        String json = new ObjectMapper().writeValueAsString(in);

        mockMvc.perform(post("/api/menuitems")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(11))
                .andExpect(jsonPath("$.category.id").value(4));
    }

    @Test
    public void testUpdate_Success() throws Exception {
        Category cat = new Category.Builder().name("App").build(); cat.setId(6L);

        MenuItem in = MenuItem.builder()
                .name("Brus")
                .price(new BigDecimal("5.00"))
                .description("Tom")
                .available(true)
                .category(cat)
                .build();

        MenuItem updated = MenuItem.builder()
                .name("Brus")
                .price(new BigDecimal("5.00"))
                .description("Tom")
                .available(true)
                .category(cat)
                .build();
        updated.setId(6L);

        given(categoryService.getById(6L)).willReturn(cat);
        given(menuItemService.update(eq(6L), any(MenuItem.class))).willReturn(updated);

        String json = new ObjectMapper().writeValueAsString(in);

        mockMvc.perform(put("/api/menuitems/6")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(6))
                .andExpect(jsonPath("$.name").value("Brus"));
    }

    @Test
    public void testUpdate_NotFound() throws Exception {
        Category cat = new Category.Builder().name("App").build(); cat.setId(7L);

        MenuItem in = MenuItem.builder()
                .name("Brus")
                .price(new BigDecimal("5.00"))
                .description("Tom")
                .available(true)
                .category(cat)
                .build();

        given(categoryService.getById(7L)).willReturn(cat);
        given(menuItemService.update(eq(7L), any(MenuItem.class))).willReturn(null);

        String json = new ObjectMapper().writeValueAsString(in);

        mockMvc.perform(put("/api/menuitems/7")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDelete() throws Exception {
        willDoNothing().given(menuItemService).delete(8L);

        mockMvc.perform(delete("/api/menuitems/8"))
                .andExpect(status().isNoContent());
    }
}
