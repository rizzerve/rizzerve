package k2.rizzerve.service;

import k2.rizzerve.model.Table;
import k2.rizzerve.repository.TableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class TableServiceTest {

    @Mock
    private TableRepository tableRepository;

    @InjectMocks
    private TableService tableService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        
        // Setup mock behavior for save method
        when(tableRepository.save(any(Table.class))).thenAnswer(invocation -> {
            Table savedTable = invocation.getArgument(0);
            if (savedTable.getId() == null) {
                savedTable.setId(1L);
            }
            return savedTable;
        });
    }

    @Test
    public void testCreateTable_Success() throws Exception {
        // Arrange
        String tableNumber = "T1";
        when(tableRepository.existsByTableNumber(tableNumber)).thenReturn(false);

        // Act
        Table table = tableService.createTable(tableNumber);

        // Assert
        assertNotNull(table);
        assertEquals(tableNumber, table.getTableNumber());
        assertFalse(table.isOccupied());
        verify(tableRepository, times(1)).save(any(Table.class));
    }

    @Test
    public void testCreateTable_TableNumberExists() {
        // Arrange
        String tableNumber = "T1";
        when(tableRepository.existsByTableNumber(tableNumber)).thenReturn(true);

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            tableService.createTable(tableNumber);
        });
        assertEquals("Table number already exists", exception.getMessage());
        verify(tableRepository, never()).save(any(Table.class));
    }

    @Test
    public void testGetAllTables() {
        // Arrange
        List<Table> mockTables = Arrays.asList(
            new Table(1L, "T1", false),
            new Table(2L, "T2", true)
        );
        when(tableRepository.findAll()).thenReturn(mockTables);

        // Act
        List<Table> result = tableService.getAllTables();

        // Assert
        assertEquals(2, result.size());
        assertEquals("T1", result.get(0).getTableNumber());
        assertEquals("T2", result.get(1).getTableNumber());
    }

    @Test
    public void testGetTableById_Success() {
        // Arrange
        Long tableId = 1L;
        Table mockTable = new Table(tableId, "T1", false);
        when(tableRepository.findById(tableId)).thenReturn(Optional.of(mockTable));

        // Act
        Optional<Table> result = tableService.getTableById(tableId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(mockTable, result.get());
    }

    @Test
    public void testGetTableById_NotFound() {
        // Arrange
        Long tableId = 999L;
        when(tableRepository.findById(tableId)).thenReturn(Optional.empty());

        // Act
        Optional<Table> result = tableService.getTableById(tableId);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    public void testUpdateTable_Success() throws Exception {
        // Arrange
        Long tableId = 1L;
        String newTableNumber = "T2";
        Table originalTable = new Table(tableId, "T1", false);
        
        when(tableRepository.findById(tableId)).thenReturn(Optional.of(originalTable));
        when(tableRepository.existsByTableNumber(newTableNumber)).thenReturn(false);

        // Act
        Optional<Table> result = tableService.updateTable(tableId, newTableNumber);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(newTableNumber, result.get().getTableNumber());
        verify(tableRepository, times(1)).save(any(Table.class));
    }    @Test
    public void testUpdateTable_TableNotFound() throws Exception {
        // Arrange
        Long tableId = 999L;
        String newTableNumber = "T2";
        
        when(tableRepository.findById(tableId)).thenReturn(Optional.empty());

        // Act
        Optional<Table> result = tableService.updateTable(tableId, newTableNumber);

        // Assert
        assertTrue(result.isEmpty());
        verify(tableRepository, never()).save(any(Table.class));
    }

    @Test
    public void testUpdateTable_TableNumberExists() {
        // Arrange
        Long tableId = 1L;
        String newTableNumber = "T2";
        Table originalTable = new Table(tableId, "T1", false);
        
        when(tableRepository.findById(tableId)).thenReturn(Optional.of(originalTable));
        when(tableRepository.existsByTableNumber(newTableNumber)).thenReturn(true);

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            tableService.updateTable(tableId, newTableNumber);
        });
        assertEquals("Table number already exists", exception.getMessage());
        verify(tableRepository, never()).save(any(Table.class));
    }

    @Test
    public void testDeleteTable_Success() {
        // Arrange
        Long tableId = 1L;
        when(tableRepository.existsById(tableId)).thenReturn(true);
        doNothing().when(tableRepository).deleteById(tableId);

        // Act
        boolean result = tableService.deleteTable(tableId);

        // Assert
        assertTrue(result);
        verify(tableRepository, times(1)).deleteById(tableId);
    }

    @Test
    public void testDeleteTable_TableNotFound() {
        // Arrange
        Long tableId = 999L;
        when(tableRepository.existsById(tableId)).thenReturn(false);

        // Act
        boolean result = tableService.deleteTable(tableId);

        // Assert
        assertFalse(result);
        verify(tableRepository, never()).deleteById(tableId);
    }
}
