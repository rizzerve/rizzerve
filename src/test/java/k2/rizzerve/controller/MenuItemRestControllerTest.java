// src/test/java/k2/rizzerve/controller/MenuItemRestControllerTest.java
package k2.rizzerve.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import k2.rizzerve.model.Category;
import k2.rizzerve.model.MenuItem;
import k2.rizzerve.service.CategoryService;
import k2.rizzerve.service.MenuItemService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MenuItemRestController.class)
class MenuItemRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MenuItemService svc;

    @MockBean
    private CategoryService catSvc;

    @Autowired
    private ObjectMapper mapper;

    @Test
    @DisplayName("GET /api/menuitems → 200 + JSON list")
    void listAll() throws Exception {
        Category c = new Category(1L, "Food");
        var items = List.of(
                new MenuItem(10L, "Burger", 5.99, c),
                new MenuItem(11L, "Fries", 2.99, c)
        );
        when(svc.listAll()).thenReturn(items);

        mockMvc.perform(get("/api/menuitems"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Burger"));

        verify(svc).listAll();
    }

    @Test
    @DisplayName("GET /api/menuitems/{id} → 200 + JSON single")
    void getOne_found() throws Exception {
        Category c = new Category(2L, "Drinks");
        MenuItem m = new MenuItem(20L, "Cola", 1.50, c);
        when(svc.getById(20L)).thenReturn(m);

        mockMvc.perform(get("/api/menuitems/20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(20))
                .andExpect(jsonPath("$.category.id").value(2));

        verify(svc).getById(20L);
    }

    @Test
    @DisplayName("GET /api/menuitems/{id} → 404 when missing")
    void getOne_notFound() throws Exception {
        when(svc.getById(99L)).thenReturn(null);

        mockMvc.perform(get("/api/menuitems/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/menuitems → 201 + JSON created")
    void create() throws Exception {
        Category c = new Category(3L, "Dessert");
        MenuItem in = new MenuItem(null, "Cake", 3.50, c);
        MenuItem saved = new MenuItem(30L, "Cake", 3.50, c);

        when(catSvc.getById(3L)).thenReturn(c);
        when(svc.add(any(MenuItem.class))).thenReturn(saved);

        mockMvc.perform(post("/api/menuitems")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(in)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(30))
                .andExpect(jsonPath("$.name").value("Cake"));

        verify(catSvc).getById(3L);
        verify(svc).add(any());
    }

    @Test
    @DisplayName("PUT /api/menuitems/{id} → 200 when exists")
    void update_found() throws Exception {
        Category c = new Category(4L, "Sides");
        MenuItem in = new MenuItem(null, "Onion Rings", 2.20, c);
        MenuItem updated = new MenuItem(40L, "Onion Rings", 2.20, c);

        when(catSvc.getById(4L)).thenReturn(c);
        when(svc.update(any(MenuItem.class))).thenReturn(updated);

        mockMvc.perform(put("/api/menuitems/40")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(in)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(40));

        verify(svc).update(any());
    }

    @Test
    @DisplayName("PUT /api/menuitems/{id} → 404 when missing")
    void update_notFound() throws Exception {
        Category c = new Category(5L, "Drinks");
        MenuItem in = new MenuItem(null, "Tea", 1.00, c);

        when(catSvc.getById(5L)).thenReturn(c);
        when(svc.update(any(MenuItem.class))).thenReturn(null);

        mockMvc.perform(put("/api/menuitems/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(in)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/menuitems/{id} → 204 when deleted")
    void delete_found() throws Exception {
        when(svc.delete(55L)).thenReturn(true);

        mockMvc.perform(delete("/api/menuitems/55"))
                .andExpect(status().isNoContent());

        verify(svc).delete(55L);
    }

    @Test
    @DisplayName("DELETE /api/menuitems/{id} → 404 when missing")
    void delete_notFound() throws Exception {
        when(svc.delete(66L)).thenReturn(false);

        mockMvc.perform(delete("/api/menuitems/66"))
                .andExpect(status().isNotFound());
    }
}
