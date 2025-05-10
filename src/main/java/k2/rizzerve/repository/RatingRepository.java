package k2.rizzerve.repository;

import k2.rizzerve.model.Rating;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class RatingRepository {

    private final Map<String, Rating> storage = new HashMap<>();

    public void save(Rating rating) {
        storage.put(rating.getId(), rating);
    }

    public Rating findById(String id) {
        return storage.get(id);
    }

    public List<Rating> findAll() {
        return new ArrayList<>(storage.values());
    }

    public List<Rating> findByMenuId(String menuId) {
        List<Rating> result = new ArrayList<>();
        for (Rating r : storage.values()) {
            if (r.getMenuId().equals(menuId)) {
                result.add(r);
            }
        }
        return result;
    }

    public Rating deleteById(String id) {
        return storage.remove(id);
    }

    public boolean existsById(String id) {
        return storage.containsKey(id);
    }

    public void update(String id, Rating updatedRating) {
        if (!storage.containsKey(id)) {
            throw new NoSuchElementException("Rating with ID " + id + " not found.");
        }
        storage.put(id, updatedRating);
    }
}
