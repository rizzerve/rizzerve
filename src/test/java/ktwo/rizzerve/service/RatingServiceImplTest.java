
package ktwo.rizzerve.service;

import ktwo.rizzerve.command.CreateRatingCommand;
import ktwo.rizzerve.command.RatingCommand;
import ktwo.rizzerve.command.UpdateRatingCommand;
import ktwo.rizzerve.model.Rating;
import ktwo.rizzerve.repository.RatingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RatingServiceImplTest {

    RatingRepository ratingRepository;
    RatingServiceImpl ratingService;

    @BeforeEach
    void setUp() {
        ratingRepository = mock(RatingRepository.class);
        SimpleMeterRegistry meterRegistry = new SimpleMeterRegistry();
        ratingService = new RatingServiceImpl(ratingRepository, meterRegistry);
        ratingService.initMetrics();
    }

    @Test
    void testExecuteCreateCommand_shouldIncrementCreateCounter() {
        CreateRatingCommand command = mock(CreateRatingCommand.class);
        Rating rating = new Rating();
        when(command.execute()).thenReturn(rating);

        Rating result = ratingService.executeCommand(command);

        assertEquals(rating, result);
        verify(command, times(1)).execute();
    }

    @Test
    void testExecuteUpdateCommand_shouldIncrementUpdateCounter() {
        UpdateRatingCommand command = mock(UpdateRatingCommand.class);
        Rating rating = new Rating();
        when(command.execute()).thenReturn(rating);

        Rating result = ratingService.executeCommand(command);

        assertEquals(rating, result);
        verify(command, times(1)).execute();
    }

    @Test
    void testGetAllRatings() {
        when(ratingRepository.findAll()).thenReturn(List.of(new Rating(), new Rating()));
        assertEquals(2, ratingService.getAll().size());
    }

    @Test
    void testGetRatingById_validUUID() {
        UUID id = UUID.randomUUID();
        Rating rating = new Rating();
        when(ratingRepository.findById(id)).thenReturn(Optional.of(rating));
        assertEquals(rating, ratingService.getById(id.toString()));
    }

    @Test
    void testGetRatingById_invalidUUID() {
        assertNull(ratingService.getById("not-a-uuid"));
    }

    @Test
    void testGetByMenu_validId() {
        when(ratingRepository.findByMenuItem_Id(1L)).thenReturn(List.of(new Rating()));
        assertEquals(1, ratingService.getByMenu("1").size());
    }

    @Test
    void testGetByMenu_invalidId() {
        assertTrue(ratingService.getByMenu("invalid").isEmpty());
    }
}
