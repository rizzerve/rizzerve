package ktwo.rizzerve.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CategoryTest {

    @Test
    void builderShouldCreateWithName() {
        String expected = "Minuman";

        Category c = new Category.Builder()
                .name(expected)
                .build();

        assertNotNull(c);
        assertNull(c.getId(), "ID should be null before persistence");
        assertEquals(expected, c.getName());
    }

    @Test
    void setterShouldModifyName() {
        Category c = new Category();
        assertNull(c.getName(), "Name should start out null");
        c.setName("Makanan Ringan");
        assertEquals("Makanan Ringan", c.getName());
    }
}
