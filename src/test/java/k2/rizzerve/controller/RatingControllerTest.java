package k2.rizzerve.controller;

import k2.rizzerve.command.CreateRatingCommand;
import k2.rizzerve.model.Rating;
import k2.rizzerve.service.RatingService;
import k2.rizzerve.strategy.FiveStarRatingValidation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RatingControllerTest {

    private RatingService ratingService;
    private RatingController controller;

    @BeforeEach
    void setUp() {
        ratingService = mock(RatingService.class);
        controller = new RatingController(ratingService);
    }

    @Test
    void testCreateRatingReturnsOk() {
        Rating dummy = new Rating("r1", "M001", "usn", 5, new FiveStarRatingValidation());

        when(ratingService.executeCommand(any(CreateRatingCommand.class))).thenReturn(dummy);

        ResponseEntity<Rating> response = controller.createRating("r1", "M001", "usn", 5);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(dummy, response.getBody());
    }
}
