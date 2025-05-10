package k2.rizzerve.repository;

import k2.rizzerve.model.Rating;
import k2.rizzerve.strategy.FiveStarRatingValidation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RatingRepositoryTest {

    private RatingRepository repository;
    private Rating rating;

    @BeforeEach
    void setUp() {
        repository = new RatingRepository();
        rating = new Rating("r1", "M001", "usn", 4, new FiveStarRatingValidation());
        repository.save(rating);
    }

    @Test
    void testFindByIdReturnsRating() {
        Rating found = repository.findById("r1");
        assertNotNull(found);
        assertEquals("M001", found.getMenuId());
    }

    @Test
    void testFindAllReturnsList() {
        List<Rating> ratings = repository.findAll();
        assertEquals(1, ratings.size());
    }

    @Test
    void testFindByMenuIdReturnsCorrectRatings() {
        List<Rating> ratings = repository.findByMenuId("M001");
        assertEquals(1, ratings.size());
        assertEquals("r1", ratings.get(0).getId());
    }

    @Test
    void testDeleteByIdRemovesRating() {
        repository.deleteById("r1");
        assertNull(repository.findById("r1"));
    }

    @Test
    void testExistsByIdReturnsTrue() {
        assertTrue(repository.existsById("r1"));
    }

    @Test
    void testExistsByIdReturnsFalse() {
        assertFalse(repository.existsById("r999"));
    }

    @Test
    void testUpdateRating() {
        Rating oldRating = new Rating("r8", "M001", "usn", 3, new FiveStarRatingValidation());
        repository.save(oldRating);

        Rating newRating = new Rating("r8", "M001", "usn", 5, new FiveStarRatingValidation());
        repository.update("r8", newRating);

        Rating result = repository.findById("r8");
        assertEquals(5, result.getRatingValue());
    }

}
