package ktwo.rizzerve.service;

import ktwo.rizzerve.command.RatingCommand;
import ktwo.rizzerve.model.Rating;
import ktwo.rizzerve.repository.RatingRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RatingServiceImplTest {

    RatingRepository repo = mock(RatingRepository.class);
    RatingServiceImpl service = new RatingServiceImpl(repo);

    @Test
    void testExecuteCommand() {
        RatingCommand cmd = mock(RatingCommand.class);
        Rating expected = new Rating();
        when(cmd.execute()).thenReturn(expected);

        Rating result = service.executeCommand(cmd);
        assertEquals(expected, result);
    }

    @Test
    void testGetAll() {
        List<Rating> ratings = Arrays.asList(new Rating(), new Rating());
        when(repo.findAll()).thenReturn(ratings);

        assertEquals(2, service.getAll().size());
    }

    @Test
    void testGetById() {
        UUID id = UUID.randomUUID();
        Rating r = new Rating();
        when(repo.findById(id)).thenReturn(Optional.of(r));

        assertEquals(r, service.getById(id.toString()));
    }

    @Test
    void testGetByMenu() {
        when(repo.findByMenuItem_Id(1L)).thenReturn(List.of(new Rating()));
        assertEquals(1, service.getByMenu("1").size());
    }
}