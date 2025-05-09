package k2.rizzerve.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import k2.rizzerve.model.Category;
import k2.rizzerve.service.CategoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryRestController.class)
@AutoConfigureMockMvc(addFilters = false)
class CategoryRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /api/categories returns list of categories")
    void testGetAll() throws Exception {
        List<Category> cats = List.of(
                new Category(1L, "Food"),
                new Category(2L, "Drinks")
        );
        when(service.listAll()).thenReturn(cats);

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(cats.size()))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Food"));

        verify(service).listAll();
    }

    @Test
    @DisplayName("GET /api/categories/{id} returns category when found")
    void testGetByIdFound() throws Exception {
        Category cat = new Category(5L, "TestCat");
        when(service.getById(5L)).thenReturn(cat);

        mockMvc.perform(get("/api/categories/5"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(5L))
                .andExpect(jsonPath("$.name").value("TestCat"));

        verify(service).getById(5L);
    }

    @Test
    @DisplayName("GET /api/categories/{id} returns 404 when not found")
    void testGetByIdNotFound() throws Exception {
        when(service.getById(99L)).thenReturn(null);

        mockMvc.perform(get("/api/categories/99"))
                .andExpect(status().isNotFound());

        verify(service).getById(99L);
    }

    @Test
    @DisplayName("POST /api/categories creates new category")
    void testCreate() throws Exception {
        Category input = new Category(null, "NewCat");
        Category saved = new Category(10L, "NewCat");
        when(service.add(any(Category.class))).thenReturn(saved);

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.name").value("NewCat"));

        verify(service).add(any(Category.class));
    }

    @Test
    @DisplayName("PUT /api/categories/{id} updates when found")
    void testUpdateFound() throws Exception {
        Category input = new Category(null, "Updated");
        Category updated = new Category(3L, "Updated");
        when(service.update(any(Category.class))).thenReturn(updated);

        mockMvc.perform(put("/api/categories/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3L))
                .andExpect(jsonPath("$.name").value("Updated"));

        verify(service).update(argThat(c -> c.getId().equals(3L) && c.getName().equals("Updated")));
    }

    @Test
    @DisplayName("PUT /api/categories/{id} returns 404 when not found")
    void testUpdateNotFound() throws Exception {
        Category input = new Category(null, "Nope");
        when(service.update(any(Category.class))).thenReturn(null);

        mockMvc.perform(put("/api/categories/7")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isNotFound());

        verify(service).update(any(Category.class));
    }

    @Test
    @DisplayName("DELETE /api/categories/{id} deletes when found")
    void testDeleteFound() throws Exception {
        when(service.delete(4L)).thenReturn(true);

        mockMvc.perform(delete("/api/categories/4"))
                .andExpect(status().isNoContent());

        verify(service).delete(4L);
    }

    @Test
    @DisplayName("DELETE /api/categories/{id} returns 404 when not found")
    void testDeleteNotFound() throws Exception {
        when(service.delete(8L)).thenReturn(false);

        mockMvc.perform(delete("/api/categories/8"))
                .andExpect(status().isNotFound());

        verify(service).delete(8L);
    }
}