package ktwo.rizzerve.command;

import ktwo.rizzerve.model.Rating;
import ktwo.rizzerve.repository.RatingRepository;
import ktwo.rizzerve.strategy.FiveStarRatingValidation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UpdateRatingCommandTest {

    private RatingRepository repository;

    @BeforeEach
    void setUp() {
        repository = new RatingRepository();
        Rating rating = new Rating("r1", "P001", "usn", 3, new FiveStarRatingValidation());
        repository.save(rating);
    }

    @Test
    void testUpdateRatingSuccess() {
        UpdateRatingCommand command = new UpdateRatingCommand(
                "r1",
                new Rating("r1", "P001", "usn", 5, new FiveStarRatingValidation()),
                repository
        );

        command.execute();

        Rating updated = repository.findById("r1");
        assertNotNull(updated);
        assertEquals(5, updated.getRatingValue());
    }

    @Test
    void testUpdateRatingNotFound() {
        UpdateRatingCommand command = new UpdateRatingCommand(
                "nonexistent",
                new Rating("nonexistent", "P002", "someone", 4, new FiveStarRatingValidation()),
                repository
        );

        assertThrows(IllegalArgumentException.class, command::execute);
    }
}
