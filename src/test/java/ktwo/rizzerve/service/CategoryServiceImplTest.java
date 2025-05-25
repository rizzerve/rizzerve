package ktwo.rizzerve.service;

import ktwo.rizzerve.controller.MenuUpdateSSEController;
import ktwo.rizzerve.model.Category;
import ktwo.rizzerve.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository repo;

    @Mock
    private MenuUpdateSSEController sseController;

    @InjectMocks
    private CategoryServiceImpl service;

    private Category cat1;
    private Category cat2;

    @BeforeEach
    void setUp() {
        cat1 = new Category();
        cat1.setId(1L);
        cat1.setName("Foo");

        cat2 = new Category();
        cat2.setId(2L);
        cat2.setName("Bar");
    }

    @Test
    void add_shouldSaveAndReturnNewCategory() {
        when(repo.save(cat1)).thenReturn(cat1);

        Category result = service.add(cat1);

        assertThat(result).isSameAs(cat1);
        verify(repo).save(cat1);
    }

    @Test
    void listAll_shouldReturnAllCategories() {
        when(repo.findAll()).thenReturn(List.of(cat1, cat2));

        List<Category> all = service.listAll();

        assertThat(all).containsExactly(cat1, cat2);
        verify(repo).findAll();
    }

    @Test
    void getById_existingId_shouldReturnCategory() {
        when(repo.findById(1L)).thenReturn(Optional.of(cat1));

        Category found = service.getById(1L);

        assertThat(found.getName()).isEqualTo("Foo");
        verify(repo).findById(1L);
    }

    @Test
    void getById_notFound_shouldThrow() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.getById(99L));
        assertThat(ex).hasMessageContaining("Category not found: 99");
    }

    @Test
    void update_existing_shouldModifyThenSave() {
        when(repo.findById(1L)).thenReturn(Optional.of(cat1));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Category updateDto = new Category();
        updateDto.setName("Updated");

        Category updated = service.update(1L, updateDto);

        assertThat(updated.getName()).isEqualTo("Updated");

        ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);
        verify(repo).save(captor.capture());
        verify(sseController).notifyAllClients();
        assertThat(captor.getValue().getName()).isEqualTo("Updated");
    }

    @Test
    void update_notFound_shouldThrow() {
        when(repo.findById(5L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> service.update(5L, new Category()));
    }

    @Test
    void delete_shouldInvokeRepository() {
        doNothing().when(repo).deleteById(2L);

        service.delete(2L);

        verify(repo).deleteById(2L);
        verify(sseController).notifyAllClients();
    }
}
