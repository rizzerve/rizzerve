package k2.rizzerve.model;

import k2.rizzerve.strategy.FiveStarRatingValidation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RatingTest {

    @Test
    void testCreateValidRating() {
        Rating rating = new Rating("r1", "M001", "usn", 5, new FiveStarRatingValidation());
        assertEquals("M001", rating.getMenuId());
        assertEquals("usn", rating.getUsername());
        assertEquals(5, rating.getRatingValue());
    }

    @Test
    void testInvalidRatingTooHigh() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Rating("r2", "M001", "usn", 10, new FiveStarRatingValidation());
        });
    }

    @Test
    void testInvalidRatingTooLow() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Rating("r3", "M001", "usn", -2, new FiveStarRatingValidation());
        });
    }
}
