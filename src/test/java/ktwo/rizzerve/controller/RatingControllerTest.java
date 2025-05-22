package ktwo.rizzerve.controller;

import ktwo.rizzerve.command.CreateRatingCommand;
import ktwo.rizzerve.model.Rating;
import ktwo.rizzerve.repository.RatingRepository;
import ktwo.rizzerve.service.RatingService;
import ktwo.rizzerve.strategy.FiveStarRatingValidation;
import ktwo.rizzerve.strategy.RatingValidationStrategy;
import ktwo.rizzerve.web.CreateRatingRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RatingControllerTest {
    private RatingService ratingService;
    private RatingRepository ratingRepository;
    private RatingValidationStrategy strategy;
    private RatingController controller;

    @BeforeEach
    void setUp() {
        ratingService    = mock(RatingService.class);
        ratingRepository = mock(RatingRepository.class);
        strategy         = new FiveStarRatingValidation();
        controller       = new RatingController(ratingService, ratingRepository, strategy);
    }

    @Test
    void testCreateReturnsCreated() {
        Rating dummy = new Rating("r1", "M001", "usn", 5, strategy);
        when(ratingService.executeCommand(any(CreateRatingCommand.class)))
                .thenReturn(dummy);

        CreateRatingRequest req = new CreateRatingRequest();
        req.setId("r1");
        req.setMenuId("M001");
        req.setUsername("usn");
        req.setRatingValue(5);

        ResponseEntity<Rating> response = controller.create(req);

        assertEquals(201, response.getStatusCodeValue());
        assertSame(dummy, response.getBody());
    }
}