package k2.rizzerve.repository;

import k2.rizzerve.model.Table;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TableRepositoryTest {

    @Autowired
    private TableRepository tableRepository;

    @Test
    @DisplayName("Should save and find table by table number")
    void testFindByTableNumber() {
        Table table = new Table();
        table.setTableNumber("T100");
        table.setOccupied(false);
        tableRepository.save(table);

        Optional<Table> found = tableRepository.findByTableNumber("T100");
        assertThat(found).isPresent();
        assertThat(found.get().getTableNumber()).isEqualTo("T100");
    }

    @Test
    @DisplayName("Should return true if table number exists")
    void testExistsByTableNumber() {
        Table table = new Table();
        table.setTableNumber("T200");
        table.setOccupied(false);
        tableRepository.save(table);

        boolean exists = tableRepository.existsByTableNumber("T200");
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false if table number does not exist")
    void testNotExistsByTableNumber() {
        boolean exists = tableRepository.existsByTableNumber("T999");
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Should find by ID")
    void testFindById() {
        Table table = new Table();
        table.setTableNumber("T300");
        table.setOccupied(true);
        Table saved = tableRepository.save(table);

        Optional<Table> found = tableRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getTableNumber()).isEqualTo("T300");
    }

    @Test
    @DisplayName("Should delete table")
    void testDeleteById() {
        Table table = new Table();
        table.setTableNumber("T400");
        table.setOccupied(false);
        Table saved = tableRepository.save(table);

        tableRepository.deleteById(saved.getId());
        Optional<Table> found = tableRepository.findById(saved.getId());
        assertThat(found).isNotPresent();
    }
}
