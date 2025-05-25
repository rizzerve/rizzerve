package ktwo.rizzerve.web;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UpdateRatingRequestTest {

    @Test
    void publicFieldsAreWritableAndReadable() {
        UpdateRatingRequest req = new UpdateRatingRequest();

        req.menuId      = "MXYZ";
        req.username    = "dan";
        req.ratingValue = 3;

        assertEquals("MXYZ",    req.menuId);
        assertEquals("dan",     req.username);
        assertEquals(3,         req.ratingValue);
    }

    @Test
    void defaultsAreNullOrZero() {
        UpdateRatingRequest req = new UpdateRatingRequest();
        assertNull(req.menuId,       "menuId should default to null");
        assertNull(req.username,     "username should default to null");
        assertEquals(0, req.ratingValue,
                "ratingValue should default to 0");
    }
}