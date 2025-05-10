package k2.rizzerve.service;

import k2.rizzerve.command.CreateRatingCommand;
import k2.rizzerve.command.DeleteRatingCommand;
import k2.rizzerve.command.UpdateRatingCommand;
import k2.rizzerve.model.Rating;
import k2.rizzerve.repository.RatingRepository;
import k2.rizzerve.strategy.FiveStarRatingValidation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RatingServiceImplTest {

    private RatingService service;
    private RatingRepository repository;

    @BeforeEach
    void setUp() {
        repository = new RatingRepository();
        service = new RatingServiceImpl();
    }

    @Test
    void testCreateRating() {
        CreateRatingCommand command = new CreateRatingCommand(
                "r1",
                "M001",
                "usn",
                5,
                new FiveStarRatingValidation(),
                repository
        );

        service.executeCommand(command);
        Rating saved = repository.findById("r1");
        assertNotNull(saved);
        assertEquals(5, saved.getRatingValue());
    }

    @Test
    void testUpdateRating() {
        Rating original = new Rating("r2", "M001", "usn", 2, new FiveStarRatingValidation());
        repository.save(original);

        Rating updated = new Rating("r2", "M001", "usn", 4, new FiveStarRatingValidation());
        UpdateRatingCommand command = new UpdateRatingCommand("r2", updated, repository);
        service.executeCommand(command);

        Rating result = repository.findById("r2");
        assertNotNull(result);
        assertEquals(4, result.getRatingValue());
    }

    @Test
    void testDeleteRating() {
        Rating rating = new Rating("r3", "M001", "usn", 3, new FiveStarRatingValidation());
        repository.save(rating);

        DeleteRatingCommand command = new DeleteRatingCommand("r3", repository);
        service.executeCommand(command);

        assertNull(repository.findById("r3"));
    }
}
