package ktwo.rizzerve.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RatingPageController.class)
class RatingPageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void showRatePage_shouldReturnRatingView() throws Exception {
        mockMvc.perform(get("/rate"))
                .andExpect(status().isOk())
                .andExpect(view().name("rating"));
    }

    @Test
    void showEditPage_shouldReturnEditView() throws Exception {
        mockMvc.perform(get("/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("edit"));
    }
}