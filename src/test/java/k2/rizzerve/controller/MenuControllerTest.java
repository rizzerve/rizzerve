package k2.rizzerve.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import k2.rizzerve.model.MenuItem;
import k2.rizzerve.dto.MenuItemDTO;
import k2.rizzerve.security.JwtUtil;
import k2.rizzerve.service.MenuService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MenuController.class)
@AutoConfigureMockMvc(addFilters = false)
public class MenuControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private MenuService menuService;

    @Autowired
    private ObjectMapper objectMapper; // used to convert Java objects to JSON

    @Test
    public void testCreateMenuItem() throws Exception {
        // Arrange
        MenuItemDTO dto = new MenuItemDTO(null, "Pasta", 11.99, "Creamy pasta", true);
        MenuItem menuItem = new MenuItem(dto.getName(), dto.getPrice(), dto.getDescription(), dto.isAvailable());
        menuItem.setId(1L);
        Mockito.when(menuService.createMenuItem(Mockito.isNull(), Mockito.any(MenuItemDTO.class)))
                .thenReturn(menuItem);

        // Act & Assert
        mockMvc.perform(post("/menu/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Pasta"));
    }

    @Test
    public void testGetFullMenu() throws Exception {
        // Return an empty list for simplicity.
        Mockito.when(menuService.getFullMenu()).thenReturn(java.util.Collections.emptyList());
        mockMvc.perform(get("/menu"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }
}
