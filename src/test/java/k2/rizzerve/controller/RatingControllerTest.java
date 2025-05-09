package k2.rizzerve.controller;

import k2.rizzerve.command.CreateRatingCommand;
import k2.rizzerve.model.Rating;
import k2.rizzerve.repository.RatingRepository;
import k2.rizzerve.service.RatingService;
import k2.rizzerve.strategy.FiveStarRatingValidation;
import k2.rizzerve.strategy.RatingValidationStrategy;
import k2.rizzerve.web.CreateRatingRequest;
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