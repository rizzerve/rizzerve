package ktwo.rizzerve.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RatingPageController.class)
@AutoConfigureMockMvc(addFilters = false)
public class RatingPageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testRatePage() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/rate"))
                .andExpect(status().isOk());
    }

    @Test
    void testEditPage() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/edit"))
                .andExpect(status().isOk());
    }

    @Test
    void testMenuRatingPage() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/rate/menu"))
                .andExpect(status().isOk());
    }

    @Test
    void testEditMenuPage() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/edit-menu"))
                .andExpect(status().isOk());
    }
}