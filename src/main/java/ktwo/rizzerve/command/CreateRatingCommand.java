package ktwo.rizzerve.command;

import ktwo.rizzerve.model.MenuItem;
import ktwo.rizzerve.model.Rating;
import ktwo.rizzerve.repository.RatingRepository;
import ktwo.rizzerve.strategy.RatingValidationStrategy;

public class CreateRatingCommand implements RatingCommand {
    private final String username;
    private final int ratingValue;
    private final RatingValidationStrategy strategy;
    private final RatingRepository repository;

    private final MenuItem menuItem;

    public CreateRatingCommand(
            MenuItem menuItem,
            String username,
            int ratingValue,
            RatingValidationStrategy strategy,
            RatingRepository repository
    ) {
        this.menuItem = menuItem;
        this.username = username;
        this.ratingValue = ratingValue;
        this.strategy = strategy;
        this.repository = repository;
    }

    @Override
    public Rating execute() {
        Rating r = new Rating(menuItem, username, ratingValue, strategy);
        return repository.save(r);
    }
}