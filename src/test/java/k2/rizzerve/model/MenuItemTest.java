package k2.rizzerve.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class MenuItemTest {

    @Test
    void builderShouldCreateAllFields() {
        String name        = "Nasi Goreng";
        BigDecimal price   = new BigDecimal("25000.50");
        String desc        = "Pedas Gurih";
        boolean available  = false;
        Category cat       = new Category.Builder().name("Makanan Berat").build();

        MenuItem item = new MenuItem.Builder()
                .name(name)
                .price(price)
                .description(desc)
                .available(available)
                .category(cat)
                .build();

        assertNotNull(item);
        assertNull(item.getId(), "ID should be null before persistence");
        assertEquals(name, item.getName());
        assertEquals(0, price.compareTo(item.getPrice()));
        assertEquals(desc, item.getDescription());
        assertEquals(available, item.isAvailable());
        assertSame(cat, item.getCategory());
    }

    @Test
    void settersShouldModifyFields() {
        MenuItem item = new MenuItem();
        assertNull(item.getName());
        assertNull(item.getPrice());
        assertNull(item.getDescription());
        assertTrue(item.isAvailable(), "default available should be true if builder default; constructor leaves as false");
        assertNull(item.getCategory());

        item.setName("Es Teh");
        item.setPrice(new BigDecimal("8000"));
        item.setDescription("Manis Dingin");
        item.setAvailable(true);
        Category cat = new Category.Builder().name("Minuman").build();
        item.setCategory(cat);

        assertEquals("Es Teh", item.getName());
        assertEquals(0, item.getPrice().compareTo(new BigDecimal("8000")));
        assertEquals("Manis Dingin", item.getDescription());
        assertTrue(item.isAvailable());
        assertSame(cat, item.getCategory());
    }
}
