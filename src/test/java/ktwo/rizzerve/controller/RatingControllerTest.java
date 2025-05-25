package ktwo.rizzerve.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ktwo.rizzerve.command.CreateRatingCommand;
import ktwo.rizzerve.command.DeleteRatingCommand;
import ktwo.rizzerve.command.UpdateRatingCommand;
import ktwo.rizzerve.model.MenuItem;
import ktwo.rizzerve.model.Rating;
import ktwo.rizzerve.repository.RatingRepository;
import ktwo.rizzerve.service.MenuItemService;
import ktwo.rizzerve.service.RatingService;
import ktwo.rizzerve.strategy.RatingValidationStrategy;
import ktwo.rizzerve.web.CreateRatingRequest;
import ktwo.rizzerve.web.UpdateRatingRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RatingController.class)
class RatingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RatingService ratingService;

    @MockBean
    private RatingRepository ratingRepository;

    @MockBean
    private RatingValidationStrategy validation;

    @MockBean
    private MenuItemService menuService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllRatings() throws Exception {
        when(ratingService.getAll()).thenReturn(List.of(new Rating()));
        mockMvc.perform(get("/api/ratings"))
                .andExpect(status().isOk());
    }

    @Test
    void getRatingsByMenu() throws Exception {
        when(ratingService.getByMenu("1")).thenReturn(List.of(new Rating()));
        mockMvc.perform(get("/api/ratings?menuId=1"))
                .andExpect(status().isOk());
    }

    @Test
    void getRatingById_found() throws Exception {
        UUID id = UUID.randomUUID();
        Rating r = new Rating();
        when(ratingService.getById(id.toString())).thenReturn(r);
        mockMvc.perform(get("/api/ratings/" + id))
                .andExpect(status().isOk());
    }

    @Test
    void getRatingById_notFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(ratingService.getById(id.toString())).thenReturn(null);
        mockMvc.perform(get("/api/ratings/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void createRating_success() throws Exception {
        CreateRatingRequest req = new CreateRatingRequest();
        req.setMenuId("1");
        req.setUsername("user");
        req.setRatingValue(5);

        MenuItem menu = new MenuItem();
        when(menuService.getById(1L)).thenReturn(menu);
        when(ratingService.executeCommand(any(CreateRatingCommand.class)))
                .thenReturn(new Rating());

        mockMvc.perform(post("/api/ratings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());
    }

    @Test
    void createRating_invalidMenuId() throws Exception {
        CreateRatingRequest req = new CreateRatingRequest();
        req.setMenuId("notanumber");
        req.setUsername("user");
        req.setRatingValue(3);

        mockMvc.perform(post("/api/ratings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateRating_success() throws Exception {
        UUID id = UUID.randomUUID();
        UpdateRatingRequest req = new UpdateRatingRequest();
        req.menuId = "1";
        req.username = "updatedUser";
        req.ratingValue = 4;

        MenuItem menu = new MenuItem();
        when(menuService.getById(1L)).thenReturn(menu);
        when(ratingService.executeCommand(any(UpdateRatingCommand.class)))
                .thenReturn(new Rating());

        mockMvc.perform(put("/api/ratings/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteRating_success() throws Exception {
        UUID id = UUID.randomUUID();
        mockMvc.perform(delete("/api/ratings/" + id))
                .andExpect(status().isNoContent());

        verify(ratingService).executeCommand(any(DeleteRatingCommand.class));
    }
}