package ktwo.rizzerve.controller;

import ktwo.rizzerve.model.MenuItem;
import ktwo.rizzerve.model.Rating;
import ktwo.rizzerve.repository.RatingRepository;
import ktwo.rizzerve.service.MenuItemService;
import ktwo.rizzerve.service.RatingService;
import ktwo.rizzerve.strategy.RatingValidationStrategy;
import ktwo.rizzerve.web.CreateRatingRequest;
import ktwo.rizzerve.web.UpdateRatingRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RatingControllerTest {

    private RatingService service;
    private RatingRepository repo;
    private RatingValidationStrategy validation;
    private MenuItemService menuService;
    private RatingController controller;

    @BeforeEach
    void setUp() {
        service = mock(RatingService.class);
        repo = mock(RatingRepository.class);
        validation = mock(RatingValidationStrategy.class);
        menuService = mock(MenuItemService.class);
        controller = new RatingController(service, repo, validation, menuService);
    }

    @Test
    void testGetAllRatings() {
        when(service.getAll()).thenReturn(Collections.emptyList());
        List<Rating> result = controller.all(null);
        assertNotNull(result);
    }

    @Test
    void testGetRatingsByMenuId() {
        when(service.getByMenu("1")).thenReturn(Collections.emptyList());
        List<Rating> result = controller.all("1");
        assertNotNull(result);
    }

    @Test
    void testGetRatingByIdNotFound() {
        when(service.getById("invalid-id")).thenReturn(null);
        ResponseEntity<Rating> res = controller.byId("invalid-id");
        assertEquals(404, res.getStatusCodeValue());
    }

    @Test
    void testCreateRatingWithInvalidMenuId() {
        CreateRatingRequest rq = new CreateRatingRequest();
        rq.setMenuId("abc"); // invalid number
        rq.setUsername("user");
        rq.setRatingValue(4);

        ResponseEntity<?> response = controller.createApi(rq);
        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("Invalid menu ID format"));
    }
}