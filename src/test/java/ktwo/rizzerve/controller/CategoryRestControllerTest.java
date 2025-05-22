package ktwo.rizzerve.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ktwo.rizzerve.model.Category;
import ktwo.rizzerve.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class CategoryRestControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryRestController categoryController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(categoryController)
                .build();
    }

    @Test
    public void testListAll() throws Exception {
        Category c1 = new Category.Builder().name("A").build(); c1.setId(1L);
        Category c2 = new Category.Builder().name("B").build(); c2.setId(2L);
        given(categoryService.listAll()).willReturn(List.of(c1, c2));

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("A"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("B"));
    }

    @Test
    public void testGetById_Success() throws Exception {
        Category c = new Category.Builder().name("X").build(); c.setId(5L);
        given(categoryService.getById(5L)).willReturn(c);

        mockMvc.perform(get("/api/categories/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.name").value("X"));
    }

    @Test
    public void testGetById_NotFound() throws Exception {
        given(categoryService.getById(9L)).willReturn(null);

        mockMvc.perform(get("/api/categories/9"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreate_Success() throws Exception {
        Category toCreate = new Category.Builder().name("New").build();
        Category saved    = new Category.Builder().name("New").build(); saved.setId(7L);
        given(categoryService.add(any(Category.class))).willReturn(saved);

        String json = new ObjectMapper().writeValueAsString(toCreate);

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.name").value("New"));
    }

    @Test
    public void testUpdate_Success() throws Exception {
        Category in      = new Category.Builder().name("Upd").build();
        Category updated = new Category.Builder().name("Upd").build(); updated.setId(3L);
        given(categoryService.update(eq(3L), any(Category.class))).willReturn(updated);

        String json = new ObjectMapper().writeValueAsString(in);

        mockMvc.perform(put("/api/categories/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.name").value("Upd"));
    }

    @Test
    public void testUpdate_NotFound() throws Exception {
        Category in = new Category.Builder().name("Upd").build();
        given(categoryService.update(eq(4L), any(Category.class))).willReturn(null);

        String json = new ObjectMapper().writeValueAsString(in);

        mockMvc.perform(put("/api/categories/4")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDelete() throws Exception {
        willDoNothing().given(categoryService).delete(8L);

        mockMvc.perform(delete("/api/categories/8"))
                .andExpect(status().isNoContent());
    }
}
