package k2.rizzerve.command;

import k2.rizzerve.model.Rating;
import k2.rizzerve.repository.RatingRepository;
import k2.rizzerve.strategy.FiveStarRatingValidation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CreateRatingCommandTest {

    @Test
    void testExecuteCreatesValidRating() {
        RatingRepository repository = new RatingRepository();

        CreateRatingCommand command = new CreateRatingCommand(
                "r1",
                "P001",
                "usn",
                5,
                new FiveStarRatingValidation(),
                repository
        );

        Rating rating = command.execute();

        assertEquals("r1", rating.getId());
        assertEquals("P001", rating.getMenuId());
        assertEquals("usn", rating.getUsername());
        assertEquals(5, rating.getRatingValue());
        assertSame(rating, repository.findById("r1"));
    }

    @Test
    void testExecuteThrowsExceptionForTooHighValue() {
        RatingRepository repository = new RatingRepository();

        CreateRatingCommand command = new CreateRatingCommand(
                "r2",
                "P002",
                "usn",
                10,
                new FiveStarRatingValidation(),
                repository
        );

        assertThrows(IllegalArgumentException.class, command::execute);
    }

    @Test
    void testExecuteThrowsExceptionForNegativeValue() {
        RatingRepository repository = new RatingRepository();

        CreateRatingCommand command = new CreateRatingCommand(
                "r3",
                "P003",
                "usn",
                -1,
                new FiveStarRatingValidation(),
                repository
        );

        assertThrows(IllegalArgumentException.class, command::execute);
    }
}
