package k2.rizzerve.service;

import k2.rizzerve.model.MenuCategory;
import k2.rizzerve.model.MenuItem;
import k2.rizzerve.model.MenuComponent;
import k2.rizzerve.dto.MenuItemDTO;
import k2.rizzerve.repository.MenuComponentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MenuServiceTest {

    @Mock
    private MenuComponentRepository menuRepo;

    @InjectMocks
    private MenuService menuService;

    @BeforeEach
    public void setUp() {
    }

    @Test
    public void testCreateCategory() {
        MenuCategory category = new MenuCategory("Drinks");
        category.setId(1L);
        when(menuRepo.save(any(MenuCategory.class))).thenReturn(category);
        MenuCategory result = menuService.createMenuCategory("Drinks");
        assertNotNull(result.getId(), "Category id should not be null");
        assertEquals("Drinks", result.getName(), "Category name should match");
        verify(menuRepo).save(any(MenuCategory.class));
    }

    @Test
    public void testCreateMenuItemWithoutCategory() {
        MenuItemDTO dto = new MenuItemDTO(null, "Burger", 9.99, "Tasty burger", true);
        MenuItem unsavedItem = new MenuItem(dto.getName(), dto.getPrice(), dto.getDescription(), dto.isAvailable());
        when(menuRepo.save(any(MenuItem.class))).thenAnswer(invocation -> {
            MenuItem savedItem = invocation.getArgument(0);
            savedItem.setId(1L);
            return savedItem;
        });
        MenuItem item = menuService.createMenuItem(null, dto);
        assertNotNull(item.getId());
        assertEquals("Burger", item.getName());
    }

    @Test
    public void testCreateMenuItemInCategory() {
        MenuCategory category = new MenuCategory("Entrees");
        category.setId(1L);
        when(menuRepo.findById(eq(1L))).thenReturn(Optional.of(category));
        when(menuRepo.save(any(MenuCategory.class))).thenAnswer(invocation -> {
            MenuCategory cat = invocation.getArgument(0);
            for (MenuComponent child : cat.getChildren()) {
                if (child instanceof MenuItem && ((MenuItem) child).getId() == null) {
                    ((MenuItem) child).setId(2L);
                }
            }
            return cat;
        });
        MenuItemDTO dto = new MenuItemDTO(null, "Pasta", 11.99, "Creamy pasta", true);
        MenuItem result = menuService.createMenuItem(category.getId(), dto);
        assertNotNull(result.getId(), "Menu item id should be assigned");
        assertEquals("Pasta", result.getName(), "Menu item name should match");
        verify(menuRepo).findById(eq(1L));
        verify(menuRepo).save(any(MenuCategory.class));
    }

    @Test
    public void testUpdateMenuItem() {
        MenuItemDTO dto = new MenuItemDTO(null, "Salad", 7.99, "Fresh salad", true);
        MenuItem item = new MenuItem(dto.getName(), dto.getPrice(), dto.getDescription(), dto.isAvailable());
        item.setId(1L);
        when(menuRepo.findById(eq(1L))).thenReturn(Optional.of(item));
        when(menuRepo.save(any(MenuItem.class))).thenAnswer(invocation -> invocation.getArgument(0));
        MenuItemDTO updatedDTO = new MenuItemDTO(1L, "Greek Salad", 8.99, "Fresh Greek salad", true);
        MenuItem updatedItem = menuService.updateMenuItem(1L, updatedDTO);
        assertEquals("Greek Salad", updatedItem.getName(), "Updated name should match");
        assertEquals(8.99, updatedItem.getPrice(), "Updated price should match");
        verify(menuRepo).findById(eq(1L));
        verify(menuRepo).save(any(MenuItem.class));
    }

    @Test
    public void testDeleteMenuItem() {
        MenuItemDTO dto = new MenuItemDTO(null, "Soup", 4.99, "Hot soup", true);
        MenuItem item = new MenuItem(dto.getName(), dto.getPrice(), dto.getDescription(), dto.isAvailable());
        item.setId(1L);
        when(menuRepo.findById(eq(1L))).thenReturn(Optional.of(item));
        doNothing().when(menuRepo).delete(item);
        menuService.deleteMenuItem(1L);
        verify(menuRepo).findById(eq(1L));
        verify(menuRepo).delete(item);
    }
}
