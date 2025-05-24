package ktwo.rizzerve.web;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CreateRatingRequestTest {

    @Test
    public void testCreateRatingRequestFields() {
        CreateRatingRequest request = new CreateRatingRequest();
        request.setId("abc123");
        request.setMenuId("menu456");
        request.setUsername("testuser");
        request.setRatingValue(5);

        assertEquals("abc123", request.getId());
        assertEquals("menu456", request.getMenuId());
        assertEquals("testuser", request.getUsername());
        assertEquals(5, request.getRatingValue());
    }
}