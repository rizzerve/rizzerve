package ktwo.rizzerve.web;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CreateRatingRequestTest {

    @Test
    void lombokGettersAndSettersWork() {
        CreateRatingRequest req = new CreateRatingRequest();
        req.setId("r123");
        req.setMenuId("M001");
        req.setUsername("alice");
        req.setRatingValue(4);

        assertEquals("r123", req.getId());
        assertEquals("M001", req.getMenuId());
        assertEquals("alice", req.getUsername());
        assertEquals(4, req.getRatingValue());
    }

    @Test
    void lombokEqualsAndHashCode() {
        CreateRatingRequest a = new CreateRatingRequest();
        a.setId("x");
        a.setMenuId("M2");
        a.setUsername("bob");
        a.setRatingValue(5);

        CreateRatingRequest b = new CreateRatingRequest();
        b.setId("x");
        b.setMenuId("M2");
        b.setUsername("bob");
        b.setRatingValue(5);

        // reflexive
        assertEquals(a, a);
        // symmetric
        assertEquals(a, b);
        assertEquals(b, a);
        // consistent hash codes
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void toStringContainsAllFields() {
        CreateRatingRequest r = new CreateRatingRequest();
        r.setId("foo");
        r.setMenuId("M3");
        r.setUsername("carol");
        r.setRatingValue(2);

        String s = r.toString();
        assertTrue(s.contains("id=foo"));
        assertTrue(s.contains("menuId=M3"));
        assertTrue(s.contains("username=carol"));
        assertTrue(s.contains("ratingValue=2"));
    }
}