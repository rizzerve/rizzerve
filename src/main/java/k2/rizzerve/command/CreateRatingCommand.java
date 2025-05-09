package k2.rizzerve.command;

import k2.rizzerve.model.Rating;
import k2.rizzerve.repository.RatingRepository;
import k2.rizzerve.strategy.RatingValidationStrategy;

public class CreateRatingCommand implements RatingCommand {
    private final String id, menuId, username;
    private final int ratingValue;
    private final RatingValidationStrategy strategy;
    private final RatingRepository repository;

    public CreateRatingCommand(
            String id,
            String menuId,
            String username,
            int ratingValue,
            RatingValidationStrategy strategy,
            RatingRepository repository
    ) {
        this.id          = id;
        this.menuId      = menuId;
        this.username    = username;
        this.ratingValue = ratingValue;
        this.strategy    = strategy;
        this.repository  = repository;
    }

    @Override
    public Rating execute() {
        // apply validation, then persist
        Rating r = new Rating(id, menuId, username, ratingValue, strategy);
        repository.save(r);
        return r;
    }
}