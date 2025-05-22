package ktwo.rizzerve.command;

import ktwo.rizzerve.model.Rating;
import ktwo.rizzerve.repository.RatingRepository;
import ktwo.rizzerve.strategy.FiveStarRatingValidation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DeleteRatingCommandTest {

    private RatingRepository repository;

    @BeforeEach
    void setUp() {
        repository = new RatingRepository();
        Rating rating = new Rating("r1", "P001", "usn", 4, new FiveStarRatingValidation());
        repository.save(rating);
    }

    @Test
    void testDeleteRatingSuccessfully() {
        DeleteRatingCommand command = new DeleteRatingCommand("r1", repository);
        command.execute();
        assertFalse(repository.existsById("r1"));
    }

    @Test
    void testDeleteRatingFailsIfNotExist() {
        DeleteRatingCommand command = new DeleteRatingCommand("nonexistent", repository);
        assertThrows(IllegalArgumentException.class, command::execute);
    }
}
