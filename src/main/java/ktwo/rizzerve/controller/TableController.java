package ktwo.rizzerve.controller;

import ktwo.rizzerve.model.Table;
import ktwo.rizzerve.service.TableService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for managing restaurant tables
 */
@RestController
@RequestMapping("/api/tables")
class TableController {

    private static final String TEXT_HTML = "text/html";
    private static final String TABLES_URL = "/tables";
    private static final String LOCATION_HEADER = "Location";
    
    private final TableService tableService;
    
    /**
     * Constructor for dependency injection
     * 
     * @param tableService The service for table operations
     */
    TableController(TableService tableService) {
        this.tableService = tableService;
    }

    /**
     * Create a new table with the given table number
     *
     * @param tableNumber The number for the new table
     * @return The created table or an error message
     */
    @PostMapping
    ResponseEntity<Object> createTable(@RequestParam(required = true) String tableNumber,
                                  @RequestHeader(value = "Accept", required = false) String accept) {
        try {
            if (tableNumber == null || tableNumber.trim().isEmpty()) {
                throw new IllegalArgumentException("Table number cannot be empty");
            }
            
            Table table = tableService.createTable(tableNumber);
            // If the request is from a browser (not API), redirect to /tables
            if (accept != null && accept.contains(TEXT_HTML)) {
                return ResponseEntity.status(302).header(LOCATION_HEADER, TABLES_URL).build();
            }
            return new ResponseEntity<>(table, HttpStatus.OK);
        } catch (Exception e) {
            if (accept != null && accept.contains(TEXT_HTML)) {
                return ResponseEntity.status(302).header(LOCATION_HEADER, TABLES_URL + "?error=" + e.getMessage()).build();
            }
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Get all tables in the restaurant
     *
     * @return List of all tables
     */
    @GetMapping
    List<Table> getAllTables() {
        return tableService.getAllTables();
    }

    /**
     * Get a specific table by ID
     *
     * @param id The ID of the table to retrieve
     * @return The table if found, or 404 Not Found
     */
    @GetMapping("/{id}")
    ResponseEntity<Table> getTableById(@PathVariable Long id) {
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
    ResponseEntity<Object> updateTable(@PathVariable Long id,
                                  @RequestParam(required = true) String newTableNumber,
                                  @RequestHeader(value = "Accept", required = false) String accept) {
        try {
            if (newTableNumber == null || newTableNumber.trim().isEmpty()) {
                throw new IllegalArgumentException("Table number cannot be empty");
            }
            
            var result = tableService.updateTable(id, newTableNumber);
            if (accept != null && accept.contains(TEXT_HTML)) {
                return ResponseEntity.status(302).header(LOCATION_HEADER, TABLES_URL).build();
            }
            if (result.isPresent()) {
                return new ResponseEntity<>(result.get(), HttpStatus.OK);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            if (accept != null && accept.contains(TEXT_HTML)) {
                return ResponseEntity.status(302).header(LOCATION_HEADER, TABLES_URL + "?error=" + e.getMessage()).build();
            }
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Delete a table
     *
     * @param id The ID of the table to delete
     * @return Success message or 404 if not found
     */
    @DeleteMapping("/{id}")
    ResponseEntity<String> deleteTable(@PathVariable Long id,
                                       @RequestHeader(value = "Accept", required = false) String accept) {
        boolean deleted = tableService.deleteTable(id);
        if (accept != null && accept.contains(TEXT_HTML)) {
            return ResponseEntity.status(302).header(LOCATION_HEADER, TABLES_URL).build();
        }
        if (deleted) {
            return ResponseEntity.ok("Table deleted successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Table not found");
        }
    }
}
