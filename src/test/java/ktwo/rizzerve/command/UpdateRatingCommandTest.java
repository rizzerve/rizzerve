package ktwo.rizzerve.command;

import ktwo.rizzerve.model.Rating;
import ktwo.rizzerve.repository.RatingRepository;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UpdateRatingCommandTest {

    @Test
    void testExecute_WhenExists() {
        UUID id = UUID.randomUUID();
        Rating updated = new Rating();
        RatingRepository repo = mock(RatingRepository.class);

        when(repo.existsById(id)).thenReturn(true);
        when(repo.save(updated)).thenReturn(updated);

        UpdateRatingCommand cmd = new UpdateRatingCommand(id.toString(), updated, repo);
        Rating result = cmd.execute();

        assertEquals(updated, result);
    }

    @Test
    void testExecute_WhenNotExists() {
        UUID id = UUID.randomUUID();
        RatingRepository repo = mock(RatingRepository.class);
        when(repo.existsById(id)).thenReturn(false);

        UpdateRatingCommand cmd = new UpdateRatingCommand(id.toString(), new Rating(), repo);
        assertNull(cmd.execute());
    }
}