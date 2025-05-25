package ktwo.rizzerve.repository;

import ktwo.rizzerve.model.MenuItem;
import ktwo.rizzerve.model.Rating;
import ktwo.rizzerve.strategy.RatingValidationStrategy;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class RatingRepositoryTest {

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Test
    void findByMenuItem_Id_shouldReturnRatings() {
        MenuItem menu = new MenuItem();
        menu.setName("Nasi Goreng");
        menu.setPrice(BigDecimal.valueOf(25000));
        menu.setDescription("Sigma");
        menu.setAvailable(true);
        menu.setCategory(null);
        menu = menuItemRepository.save(menu);

        RatingValidationStrategy strategy = r -> {}; // dummy validation
        Rating rating = new Rating(menu, "okkk", 4, strategy);
        ratingRepository.save(rating);

        // When
        List<Rating> found = ratingRepository.findByMenuItem_Id(menu.getId());

        // Then
        assertEquals(1, found.size());
        assertEquals("okkk", found.get(0).getUsername());
        assertEquals(4, found.get(0).getRatingValue());
        assertEquals(menu.getId(), found.get(0).getMenuItem().getId());
    }

    @Test
    void findByMenuItem_Id_shouldReturnEmpty_whenNoMatch() {
        List<Rating> result = ratingRepository.findByMenuItem_Id(999L);
        assertTrue(result.isEmpty());
    }
}