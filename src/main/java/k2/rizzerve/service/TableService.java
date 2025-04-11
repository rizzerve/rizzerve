package k2.rizzerve.service;

import k2.rizzerve.model.Table;
import k2.rizzerve.repository.TableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service class for restaurant table management operations
 */
@Service
public class TableService {

    private final TableRepository tableRepository;

    @Autowired
    public TableService(TableRepository tableRepository) {
        this.tableRepository = tableRepository;
    }

    /**
     * Creates a new restaurant table with the given table number
     * 
     * @param tableNumber the table number for the new table
     * @return the newly created table
     * @throws Exception if a table with the same number already exists
     */
    public Table createTable(String tableNumber) throws Exception {
        if (tableRepository.existsByTableNumber(tableNumber)) {
            throw new Exception("Table number already exists");
        }

        Table table = new Table(tableNumber);
        return tableRepository.save(table);
    }

    /**
     * Retrieves all tables in the restaurant
     * 
     * @return list of all tables
     */
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
        
        return Optional.of(tableRepository.save(table));
    }

    /**
     * Deletes a table by its ID
     * 
     * @param id the ID of the table to delete
     * @return true if the table was deleted, false if it didn't exist
     */
    public boolean deleteTable(Long id) {
        if (tableRepository.existsById(id)) {
            tableRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
