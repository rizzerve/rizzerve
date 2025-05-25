package ktwo.rizzerve.command;

import ktwo.rizzerve.model.Rating;
import ktwo.rizzerve.repository.RatingRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class DeleteRatingCommandTest {

    @Test
    void testExecute() {
        UUID id = UUID.randomUUID();
        RatingRepository repo = mock(RatingRepository.class);
        Rating rating = new Rating();
        when(repo.findById(id)).thenReturn(Optional.of(rating));

        DeleteRatingCommand cmd = new DeleteRatingCommand(id.toString(), repo);
        Rating result = cmd.execute();

        verify(repo).deleteById(id);
        assertEquals(rating, result);
    }
}