package ktwo.rizzerve.service;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import ktwo.rizzerve.controller.MenuUpdateSSEController;
import ktwo.rizzerve.model.MenuItem;
import ktwo.rizzerve.repository.MenuItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenuItemServiceImplTest {
    @Mock
    private MenuItemRepository repo;

    @Mock
    private MenuUpdateSSEController sseController;

    private MenuItemServiceImpl service;

    @BeforeEach
    void setUp() {
        MeterRegistry registry = new SimpleMeterRegistry();
        service = new MenuItemServiceImpl(repo, sseController, registry);
    }

    @Test
    void add_ShouldReturnSavedItem() {
        MenuItem item = new MenuItem.Builder()
                .name("Bakso")
                .price(new BigDecimal("15000"))
                .description("Kuah panas")
                .build();
        when(repo.save(item)).thenReturn(item);

        MenuItem result = service.add(item);

        assertThat(result).isSameAs(item);
        verify(repo).save(item);
        verify(sseController).notifyAllClients(); // SSE triggered
    }

    @Test
    void listAll_ShouldReturnAllItems() {
        List<MenuItem> list = List.of(
                new MenuItem.Builder().name("Sate").price(new BigDecimal("20000")).build()
        );
        when(repo.findAll()).thenReturn(list);

        var result = service.listAll();

        assertThat(result).hasSize(1).containsExactlyElementsOf(list);
        verify(repo).findAll();
    }

    @Test
    void getById_WhenExists_ShouldReturnItem() {
        MenuItem item = new MenuItem.Builder().name("Nasi").price(BigDecimal.ONE).build();
        when(repo.findById(1L)).thenReturn(Optional.of(item));

        var result = service.getById(1L);

        assertThat(result).isSameAs(item);
        verify(repo).findById(1L);
    }

    @Test
    void getById_WhenMissing_ShouldThrow() {
        when(repo.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(2L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("MenuItem not found");
        verify(repo).findById(2L);
    }

    @Test
    void update_ShouldModifyAndSaveAndTriggerSSE() {
        MenuItem existing = new MenuItem.Builder()
                .name("Old")
                .price(BigDecimal.ONE)
                .description("old")
                .build();
        when(repo.findById(1L)).thenReturn(Optional.of(existing));
        when(repo.save(existing)).thenReturn(existing);

        MenuItem data = new MenuItem.Builder()
                .name("New")
                .price(new BigDecimal("5000"))
                .description("Baru")
                .available(false)
                .build();

        var updated = service.update(1L, data);

        assertThat(updated.getName()).isEqualTo("New");
        assertThat(updated.getPrice()).isEqualByComparingTo("5000");
        assertThat(updated.getDescription()).isEqualTo("Baru");
        assertThat(updated.isAvailable()).isFalse();

        verify(repo).save(existing);
        verify(sseController).notifyAllClients();
    }

    @Test
    void delete_ShouldCallRepoAndTriggerSSE() {
        service.delete(3L);

        verify(repo).deleteById(3L);
        verify(sseController).notifyAllClients();
    }
}
