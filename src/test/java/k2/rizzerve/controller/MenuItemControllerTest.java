package k2.rizzerve.controller;

import k2.rizzerve.model.MenuItem;
import k2.rizzerve.service.MenuItemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MenuItemController.class)
class MenuItemControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private MenuItemService service;

    @Test
    void list_ShouldShowListView() throws Exception {
        when(service.listAll()).thenReturn(List.of(
                new MenuItem.Builder().name("Mie").price(BigDecimal.ONE).build()
        ));

        mvc.perform(get("/menu"))
                .andExpect(status().isOk())
                .andExpect(view().name("menu_list"))
                .andExpect(model().attributeExists("items"));
    }

    @Test
    void newForm_ShouldShowForm() throws Exception {
        mvc.perform(get("/menu/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("menu_form"))
                .andExpect(model().attributeExists("menuItem"));
    }

    @Test
    void save_ShouldRedirectAndCallService() throws Exception {
        mvc.perform(post("/menu/save")
                        .param("name", "Test")
                        .param("price", "123.45")
                        .param("description", "desc")
                        .param("available", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/menu"));

        verify(service).add(any(MenuItem.class));
    }

    @Test
    void editForm_ShouldPopulateForm() throws Exception {
        MenuItem item = new MenuItem.Builder().name("E").price(BigDecimal.TEN).build();
        when(service.getById(5L)).thenReturn(item);

        mvc.perform(get("/menu/edit/5"))
                .andExpect(status().isOk())
                .andExpect(view().name("menu_form"))
                .andExpect(model().attribute("menuItem", item));
    }

    @Test
    void delete_ShouldRedirectAndCallService() throws Exception {
        mvc.perform(get("/menu/delete/7"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/menu"));

        verify(service).delete(7L);
    }
}
