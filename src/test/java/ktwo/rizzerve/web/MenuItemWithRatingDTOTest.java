package ktwo.rizzerve.web;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MenuItemWithRatingDTOTest {

    @Test
    public void testMenuItemWithRatingDTOFields() {
        MenuItemWithRatingDTO dto = new MenuItemWithRatingDTO();
        dto.setId(1L);
        dto.setName("Nasi Goreng");
        dto.setDescription("Enak dan pedas");
        dto.setPrice(20000);
        dto.setAvailable(true);
        dto.setAverageRating(4.5);

        MenuItemWithRatingDTO.CategoryDTO cat = new MenuItemWithRatingDTO.CategoryDTO();
        cat.setId(10L);
        cat.setName("Main Course");

        dto.setCategory(cat);

        assertEquals(1L, dto.getId());
        assertEquals("Nasi Goreng", dto.getName());
        assertEquals("Enak dan pedas", dto.getDescription());
        assertEquals(20000, dto.getPrice());
        assertTrue(dto.isAvailable());
        assertEquals(4.5, dto.getAverageRating());

        assertNotNull(dto.getCategory());
        assertEquals(10L, dto.getCategory().getId());
        assertEquals("Main Course", dto.getCategory().getName());
    }
}