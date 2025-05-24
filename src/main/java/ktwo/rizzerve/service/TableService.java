package ktwo.rizzerve.service;

import ktwo.rizzerve.model.Table;
import ktwo.rizzerve.repository.TableRepository;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service class for restaurant table management operations
 */
@Service
public class TableService {

    private final TableRepository tableRepository;
    private final MeterRegistry meterRegistry;

    /**
     * Constructor for tests or default registry.
     */
    public TableService(TableRepository tableRepository) {
        this(tableRepository, new SimpleMeterRegistry());
    }

    @Autowired
    public TableService(TableRepository tableRepository, MeterRegistry meterRegistry) {
        this.tableRepository = tableRepository;
        // use provided registry or fallback for tests
        this.meterRegistry = meterRegistry != null ? meterRegistry : new SimpleMeterRegistry();
    }

    @PostConstruct
    public void initMetrics() {
        // Gauge for current total number of tables
        Gauge.builder("tables_total_count", tableRepository, TableRepository::count)
             .description("Total number of restaurant tables")
             .register(meterRegistry);
    }

    /**
     * Creates a new restaurant table with the given table number
     * 
     * @param tableNumber the table number for the new table
     * @return the newly created table
     * @throws Exception if a table with the same number already exists
     */
    @Timed(value = "tables_create_duration", description = "Duration to create a table")
    public Table createTable(String tableNumber) throws Exception {
        if (tableRepository.existsByTableNumber(tableNumber)) {
            throw new Exception("Table number already exists");
        }

        Table table = new Table(tableNumber);
        Table saved = tableRepository.save(table);
        meterRegistry.counter("tables_created_total").increment();
        return saved;
    }

    /**
     * Retrieves all tables in the restaurant
     * 
     * @return list of all tables
     */
    @Timed(value = "tables_list_duration", description = "Duration to list all tables")
    public List<Table> getAllTables() {
        return tableRepository.findAll();
    }

    /**
     * Finds a table by its ID
     * 
     * @param id the ID of the table to find
     * @return an Optional containing the found table, or empty if not found
     */
    public Optional<Table> getTableById(Long id) {
        return tableRepository.findById(id);
    }

    /**
     * Updates a table's number
     * 
     * @param id the ID of the table to update
     * @param newTableNumber the new table number to set
     * @return an Optional with the updated table, or empty if the table was not found
     * @throws Exception if the new table number already exists
     */
    @Timed(value = "tables_update_duration", description = "Duration to update a table")
    public Optional<Table> updateTable(Long id, String newTableNumber) throws Exception {
        Optional<Table> tableOpt = tableRepository.findById(id);
        
        if (tableOpt.isEmpty()) {
            return Optional.empty();
        }
        
        // Check if the new table number already exists (and it's not this table's current number)
        if (!tableOpt.get().getTableNumber().equals(newTableNumber) && 
            tableRepository.existsByTableNumber(newTableNumber)) {
            throw new Exception("Table number already exists");
        }
        
        Table table = tableOpt.get();
        table.setTableNumber(newTableNumber);
        
        Table saved = tableRepository.save(table);
        meterRegistry.counter("tables_updated_total").increment();
        return Optional.of(saved);
    }

    /**
     * Deletes a table by its ID
     * 
     * @param id the ID of the table to delete
     * @return true if the table was deleted, false if it didn't exist
     */
    @Timed(value = "tables_delete_duration", description = "Duration to delete a table")
    public boolean deleteTable(Long id) {
        if (tableRepository.existsById(id)) {
            tableRepository.deleteById(id);
            meterRegistry.counter("tables_deleted_total").increment();
            return true;
        }
        return false;
    }
}
