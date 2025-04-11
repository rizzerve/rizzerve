package k2.rizzerve.controller;

import k2.rizzerve.model.Table;
import k2.rizzerve.service.TableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for managing restaurant tables
 */
@RestController
@RequestMapping("/api/tables")
public class TableController {

    @Autowired
    private TableService tableService;

    /**
     * Create a new table with the given table number
     *
     * @param tableNumber The number for the new table
     * @return The created table or an error message
     */
    @PostMapping
    public ResponseEntity<?> createTable(@RequestParam String tableNumber) {
        try {
            Table table = tableService.createTable(tableNumber);
            return ResponseEntity.ok(table);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Get all tables in the restaurant
     *
     * @return List of all tables
     */
    @GetMapping
    public List<Table> getAllTables() {
        return tableService.getAllTables();
    }

    /**
     * Get a specific table by ID
     *
     * @param id The ID of the table to retrieve
     * @return The table if found, or 404 Not Found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Table> getTableById(@PathVariable Long id) {
        return tableService.getTableById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Update a table's number
     *
     * @param id The ID of the table to update
     * @param newTableNumber The new table number
     * @return The updated table or an error message
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTable(@PathVariable Long id, @RequestParam String newTableNumber) {
        try {
            return tableService.updateTable(id, newTableNumber)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Delete a table
     *
     * @param id The ID of the table to delete
     * @return Success message or 404 if not found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTable(@PathVariable Long id) {
        boolean deleted = tableService.deleteTable(id);
        if (deleted) {
            return ResponseEntity.ok("Table deleted successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Table not found");
        }
    }
}
