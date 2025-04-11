package k2.rizzerve.repository;

import k2.rizzerve.model.Table;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Table entity operations
 */
@Repository
public interface TableRepository extends JpaRepository<Table, Long> {
    
    /**
     * Find a table by its table number
     * @param tableNumber The table number to search for
     * @return An Optional containing the table if found
     */
    Optional<Table> findByTableNumber(String tableNumber);
    
    /**
     * Check if a table with the given table number exists
     * @param tableNumber The table number to check
     * @return True if a table with the given number exists, false otherwise
     */
    boolean existsByTableNumber(String tableNumber);
}
