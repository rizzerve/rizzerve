package ktwo.rizzerve.command;

import ktwo.rizzerve.model.MenuItem;
import ktwo.rizzerve.model.Rating;
import ktwo.rizzerve.repository.RatingRepository;
import ktwo.rizzerve.strategy.RatingValidationStrategy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CreateRatingCommandTest {

    @Test
    void testExecute() {
        MenuItem menu = new MenuItem();
        Rating expected = new Rating();
        RatingRepository repo = mock(RatingRepository.class);
        RatingValidationStrategy strategy = mock(RatingValidationStrategy.class);

        when(repo.save(any())).thenReturn(expected);

        CreateRatingCommand cmd = new CreateRatingCommand(menu, "user", 4, strategy, repo);
        Rating result = cmd.execute();

        verify(strategy).validate(any());
        verify(repo).save(any());
        assertEquals(expected, result);
    }
}