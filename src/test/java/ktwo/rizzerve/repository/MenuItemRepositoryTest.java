package ktwo.rizzerve.repository;

import ktwo.rizzerve.model.Category;
import ktwo.rizzerve.model.MenuItem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.ANY)
class MenuItemRepositoryTest {

    @Autowired
    private MenuItemRepository itemRepo;

    @Autowired
    private CategoryRepository catRepo;

    @Test
    @DisplayName("save() persists MenuItem with its Category")
    void saveShouldPersistWithCategory() {
        Category cat = catRepo.save(new Category.Builder().name("Makanan Berat").build());
        MenuItem item = new MenuItem.Builder()
                .name("Nasi Goreng")
                .price(new BigDecimal("20000"))
                .description("Enak")
                .available(true)
                .category(cat)
                .build();

        MenuItem saved = itemRepo.save(item);

        assertThat(saved.getId()).isNotNull();
        MenuItem found = itemRepo.findById(saved.getId()).orElseThrow();
        assertThat(found.getCategory().getId()).isEqualTo(cat.getId());
    }

    @Test
    @DisplayName("findAll() returns all items and their categories")
    void findAllShouldReturnItems() {
        Category cat = catRepo.save(new Category.Builder().name("Minuman").build());
        itemRepo.save(new MenuItem.Builder()
                .name("Es Teh")
                .price(new BigDecimal("5000"))
                .description("Dingin")
                .available(true)
                .category(cat)
                .build()
        );

        List<MenuItem> all = itemRepo.findAll();
        assertThat(all).hasSize(1)
                .allMatch(mi -> mi.getCategory().getName().equals("Minuman"));
    }
}
