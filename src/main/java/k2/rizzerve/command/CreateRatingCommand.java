package k2.rizzerve.command;

import k2.rizzerve.model.Rating;
import k2.rizzerve.strategy.RatingValidationStrategy;

public class CreateRatingCommand implements RatingCommand {

    private final String id;
    private final String menuId;
    private final String username;
    private final int ratingValue;
    private final RatingValidationStrategy strategy;

    public CreateRatingCommand(String id, String menuId, String username, int ratingValue, RatingValidationStrategy strategy) {
        this.id = id;
        this.menuId = menuId;
        this.username = username;
        this.ratingValue = ratingValue;
        this.strategy = strategy;
    }

    @Override
    public Rating execute() {
        return new Rating(id, menuId, username, ratingValue, strategy);
    }
}
