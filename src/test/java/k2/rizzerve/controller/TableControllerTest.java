package k2.rizzerve.controller;

import k2.rizzerve.model.Table;
import k2.rizzerve.service.TableService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class TableControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TableService tableService;

    @InjectMocks
    private TableController tableController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(tableController).build();
    }

    @Test
    public void testCreateTable_Success() throws Exception {
        // Arrange
        Table mockTable = new Table(1L, "T1", false);
        when(tableService.createTable(anyString())).thenReturn(mockTable);

        // Act & Assert
        mockMvc.perform(post("/api/tables")
                .param("tableNumber", "T1")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tableNumber").value("T1"))
                .andExpect(jsonPath("$.occupied").value(false));
    }

    @Test
    public void testCreateTable_TableNumberExists() throws Exception {
        // Arrange
        when(tableService.createTable(anyString())).thenThrow(new Exception("Table number already exists"));

        // Act & Assert
        mockMvc.perform(post("/api/tables")
                .param("tableNumber", "T1")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Table number already exists"));
    }

    @Test
    public void testGetAllTables() throws Exception {
        // Arrange
        List<Table> mockTables = Arrays.asList(
            new Table(1L, "T1", false),
            new Table(2L, "T2", true)
        );
        when(tableService.getAllTables()).thenReturn(mockTables);

        // Act & Assert
        mockMvc.perform(get("/api/tables"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tableNumber").value("T1"))
                .andExpect(jsonPath("$[1].tableNumber").value("T2"))
                .andExpect(jsonPath("$[1].occupied").value(true));
    }

    @Test
    public void testGetTableById_Success() throws Exception {
        // Arrange
        Table mockTable = new Table(1L, "T1", false);
        when(tableService.getTableById(anyLong())).thenReturn(Optional.of(mockTable));

        // Act & Assert
        mockMvc.perform(get("/api/tables/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tableNumber").value("T1"));
    }

    @Test
    public void testGetTableById_NotFound() throws Exception {
        // Arrange
        when(tableService.getTableById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/tables/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateTable_Success() throws Exception {
        // Arrange
        Table mockTable = new Table(1L, "T2", false);
        when(tableService.updateTable(anyLong(), anyString())).thenReturn(Optional.of(mockTable));

        // Act & Assert
        mockMvc.perform(put("/api/tables/1")
                .param("newTableNumber", "T2")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tableNumber").value("T2"));
    }

    @Test
    public void testUpdateTable_NotFound() throws Exception {
        // Arrange
        when(tableService.updateTable(anyLong(), anyString())).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(put("/api/tables/999")
                .param("newTableNumber", "T2")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateTable_TableNumberExists() throws Exception {
        // Arrange
        when(tableService.updateTable(anyLong(), anyString())).thenThrow(new Exception("Table number already exists"));

        // Act & Assert
        mockMvc.perform(put("/api/tables/1")
                .param("newTableNumber", "T2")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Table number already exists"));
    }

    @Test
    public void testDeleteTable_Success() throws Exception {
        // Arrange
        when(tableService.deleteTable(anyLong())).thenReturn(true);

        // Act & Assert
        mockMvc.perform(delete("/api/tables/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Table deleted successfully"));
    }

    @Test
    public void testDeleteTable_NotFound() throws Exception {
        // Arrange
        when(tableService.deleteTable(anyLong())).thenReturn(false);

        // Act & Assert
        mockMvc.perform(delete("/api/tables/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Table not found"));
    }
}
