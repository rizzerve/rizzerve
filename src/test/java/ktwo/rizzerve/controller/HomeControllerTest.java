package ktwo.rizzerve.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get; // ⬅️ you missed this
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status; // ⬅️ this too
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

@WebMvcTest(HomeController.class)
@AutoConfigureMockMvc(addFilters = false)
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testPublicHomePage_Unauthenticated() throws Exception {
        mockMvc.perform(get("/home"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"));
    }

    @Test
    void testPublicHomePage_Authenticated() throws Exception {
        mockMvc.perform(get("/home").principal(() -> "user"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("admin/home"));
    }

    @Test
    void testAdminHomePage() throws Exception {
        mockMvc.perform(get("/admin/home"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin_home"));
    }
}
