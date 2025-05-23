package ktwo.rizzerve.strategy;

import ktwo.rizzerve.model.Rating;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FiveStarRatingValidationTest {

    FiveStarRatingValidation validator = new FiveStarRatingValidation();

    @Test
    void testValidRating() {
        Rating rating = new Rating();
        rating.setRatingValue(5);
        assertDoesNotThrow(() -> validator.validate(rating));
    }

    @Test
    void testInvalidLow() {
        Rating rating = new Rating();
        rating.setRatingValue(0);
        assertThrows(IllegalArgumentException.class, () -> validator.validate(rating));
    }

    @Test
    void testInvalidHigh() {
        Rating rating = new Rating();
        rating.setRatingValue(6);
        assertThrows(IllegalArgumentException.class, () -> validator.validate(rating));
    }
}