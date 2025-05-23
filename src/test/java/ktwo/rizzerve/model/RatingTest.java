package ktwo.rizzerve.model;

import ktwo.rizzerve.strategy.RatingValidationStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RatingTest {

    private RatingValidationStrategy validation;
    private MenuItem dummyMenu;

    @BeforeEach
    void setUp() {
        validation = mock(RatingValidationStrategy.class);
        dummyMenu = new MenuItem();
    }

    @Test
    void constructor_shouldSetFields_andCallValidation() {
        Rating rating = new Rating(dummyMenu, "michelle", 4, validation);

        assertEquals("michelle", rating.getUsername());
        assertEquals(4, rating.getRatingValue());
        assertEquals(dummyMenu, rating.getMenuItem());

        verify(validation).validate(rating);
    }

    @Test
    void constructorWithId_shouldSetFields() {
        UUID id = UUID.randomUUID();
        MenuItem menu = new MenuItem(); // atau mock(MenuItem.class);
        RatingValidationStrategy validation = mock(RatingValidationStrategy.class);

        Rating r = new Rating(id, menu, "user", 5, validation);

        assertEquals(id, r.getId());
        assertEquals(menu, r.getMenuItem());
        assertEquals("user", r.getUsername());
        assertEquals(5, r.getRatingValue());

        verify(validation).validate(r);
    }

    @Test
    void ratingValue_outOfRange_shouldThrowFromValidation() {
        doThrow(new IllegalArgumentException("invalid"))
                .when(validation).validate(any());

        assertThrows(IllegalArgumentException.class, () ->
                new Rating(dummyMenu, "x", 10, validation));
    }
}