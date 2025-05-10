package k2.rizzerve.model;

import k2.rizzerve.strategy.RatingValidationStrategy;
import lombok.Getter;

@Getter
public class Rating {
    private final String id;
    private final String menuId;
    private final String username;
    private final int ratingValue;

    public Rating(String id, String menuId, String username, int ratingValue, RatingValidationStrategy strategy) {
        if (!strategy.isValid(ratingValue)) {
            throw new IllegalArgumentException("Invalid rating value according to strategy");
        }
        this.id = id;
        this.menuId = menuId;
        this.username = username;
        this.ratingValue = ratingValue;
    }
}

