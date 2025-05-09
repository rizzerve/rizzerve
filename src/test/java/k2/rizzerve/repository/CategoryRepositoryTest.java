package k2.rizzerve.repository;

import k2.rizzerve.model.Category;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository repo;

    @Test
    @DisplayName("save() persists and sets ID")
    void saveShouldPersistCategory() {
        Category cat = new Category.Builder().name("Minuman").build();
        Category saved = repo.save(cat);

        assertThat(saved.getId()).isNotNull();
        assertThat(repo.findById(saved.getId())).isPresent();
    }

    @Test
    @DisplayName("findById() returns empty for unknown ID")
    void findByIdShouldReturnEmpty() {
        Optional<Category> none = repo.findById(999L);
        assertThat(none).isEmpty();
    }

    @Test
    @DisplayName("deleteById() actually removes the entity")
    void deleteShouldRemoveEntity() {
        Category cat = repo.save(new Category.Builder().name("Snack").build());
        Long id = cat.getId();
        repo.deleteById(id);
        assertThat(repo.findById(id)).isEmpty();
    }
}
